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
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.saraga.workoutapp.MainActivity
import com.saraga.workoutapp.R
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_PAUSE_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_SHOW_TRACKER_FRAGMENT
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.FASTEST_LOCATION_INTERVAL
import com.saraga.workoutapp.utils.Constants.Companion.LOCATION_UPDATE_INTERVAL
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.saraga.workoutapp.utils.Constants.Companion.NOTIFICATION_ID
import com.saraga.workoutapp.utils.TrackingUtility.locationPermissionApproved

class TrackingService : LifecycleService() {

    var isFirstRun: Boolean = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }

    private fun initValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
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
                Log.d(TAG, "NEW LOCATION: ${location.latitude}, ${location.longitude}")
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

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isTracking.value!!) {
                locationResult.locations.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocation(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        Log.d(TAG, "Starting Service")
                        isFirstRun = false
                        startForegroundService()
                    } else {
                        Log.d(TAG, "Resuming Service")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "Pausing Service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Stopping Service")
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