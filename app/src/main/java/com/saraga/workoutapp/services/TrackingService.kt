package com.saraga.workoutapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.saraga.workoutapp.MainActivity
import com.saraga.workoutapp.R
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_SHOW_TRACKER_FRAGMENT
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.FASTEST_LOCATION_INTERVAL
import com.saraga.workoutapp.utils.Constants.Companion.LOCATION_UPDATE_INTERVAL
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_ID
import com.saraga.workoutapp.utils.Constants.Companion.TIMER_UPDATE_INTERVAL
import com.saraga.workoutapp.utils.TrackingUtility.getFormattedTimestamp
import com.saraga.workoutapp.utils.TrackingUtility.locationPermissionApproved
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val timeRunInSeconds = MutableLiveData<Long>()
    private var timeStarted = 0L
    private var elapsedTime = 0L
    private var lastSecondElapsed = 0L
    private var serviceStopped = false

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val bearing = MutableLiveData<Float>()
        val timeRunInMillis = MutableLiveData<Long>()
    }

    private fun initValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        bearing.postValue(0f)
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                elapsedTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(elapsedTime)
                if (timeRunInMillis.value!! >= lastSecondElapsed + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondElapsed += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun addEmptyPolylines() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean) {
        if (isTracking) {
            if (locationPermissionApproved(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (timeStarted == 0L) {
                startTimer()
            }
            if (isTracking.value!!) {
                locationResult.locations.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                        bearing.postValue(location.bearing)
                    }
                }
            }
        }
    }

    private fun stopService() {
        serviceStopped = true
        initValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        initValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocation(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_SERVICE -> {
                    Log.d(TAG, "Starting Service")
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Stopping Service")
                    stopService()
                }
                else -> {
                    Log.d(TAG, "Unknown Action")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        addEmptyPolylines()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_running)
            .setContentTitle("Nuke Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        timeRunInSeconds.observe(this, {
            if (!serviceStopped){
                val timerNotificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_running)
                    .setContentTitle("Nuke Running App")
                    .setContentText(getFormattedTimestamp(it * 1000L))
                    .setContentIntent(getMainActivityPendingIntent())

                notificationManager.notify(NOTIFICATION_ID, timerNotificationBuilder.build())
            }
        })
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKER_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManager) {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                setShowBadge(false)
            }

        notificationChannel.description = getString(R.string.notification_channel_description)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

private const val TAG = "TrackingService"