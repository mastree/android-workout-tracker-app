package com.saraga.workoutapp.tracker

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.saraga.workoutapp.BuildConfig
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.services.Polyline
import com.saraga.workoutapp.services.TrackingService
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.BACKGROUND_LOCATION_PERMISSION_INDEX
import com.saraga.workoutapp.utils.Constants.Companion.LOCATION_PERMISSION_INDEX
import com.saraga.workoutapp.utils.Constants.Companion.MAP_ZOOM
import com.saraga.workoutapp.utils.Constants.Companion.MIN_MAP_ZOOM
import com.saraga.workoutapp.utils.Constants.Companion.POLYLINE_COLOR
import com.saraga.workoutapp.utils.Constants.Companion.POLYLINE_WIDTH
import com.saraga.workoutapp.utils.Constants.Companion.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.saraga.workoutapp.utils.Constants.Companion.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.saraga.workoutapp.utils.TrackingUtility.calculatePolylineDistance
import com.saraga.workoutapp.utils.TrackingUtility.getAvgSpeed
import com.saraga.workoutapp.utils.TrackingUtility.getFormattedTimestamp
import com.saraga.workoutapp.utils.TrackingUtility.getStepsFromMeter
import com.saraga.workoutapp.utils.TrackingUtility.locationPermissionApproved
import com.saraga.workoutapp.utils.TrackingUtility.runningQOrLater
import kotlinx.android.synthetic.main.fragment_training_tracker.*
import java.util.*

class Tracker() : Fragment() {
    private val trackViewModel: TrackerViewModel by viewModels {
        TrackerViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    private var intentRequestTrackerCode: Int = 0

    private var map: GoogleMap? = null
    private var tracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var bearing = 0f

    private var totalDistance = 0
    private var totalDuration = 0L
    private var isCycling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            intentRequestTrackerCode = it.getInt("intentRequestTrackerCode", 0)
            Log.d(TAG, "Get intentRequestTrackerCode: $intentRequestTrackerCode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_tracker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        startStopButton.setOnClickListener {
            toggleRun()
            cyclingButton.isClickable = false
            runningButton.isClickable = false
        }
        toggleTypeButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.cyclingButton) {
                    stepsText.visibility = GONE
                    isCycling = true
                } else {
                    stepsText.visibility = VISIBLE
                    isCycling = false
                }
            }
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        updateUiData()
        subscribeToObservers()
        if (intentRequestTrackerCode == 1) {
            toggleRun()
            cyclingButton.isClickable = false
            runningButton.isClickable = false
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
            if (pathPoints.isNotEmpty()){
                updateUiData()
            }
        })
        TrackingService.bearing.observe(viewLifecycleOwner, {
            bearing = it
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, {
            if (it != 0L){
                totalDuration = it
                val curTimeInMillis = getFormattedTimestamp(it, true)
                timerText.text = curTimeInMillis
            }
        })
    }

    private fun toggleRun() {
        if (tracking){
            startStopButton.text = getString(R.string.start)
            sendCommandToService(ACTION_STOP_SERVICE)
            zoomOutWholeRoute()
            saveToDb()
        } else {
            sendCommandToService(ACTION_START_SERVICE)
            startStopButton.text = getString(R.string.stop)
            timerText.text = getString(R.string.loading)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        tracking = isTracking
    }

    private fun updateUiData() {
        var distance = 0
        for (polyline in pathPoints) {
            distance += calculatePolylineDistance(polyline).toInt()
        }
        totalDistance = distance
        val speed = getAvgSpeed(totalDistance, totalDuration)
        val distDisplayed = totalDistance.toString() + " " + getString(R.string.distance_unit)
        val speedDisplayed = speed.toString() + " " + getString(R.string.speed_unit)
        distanceText.text = distDisplayed
        speedText.text = speedDisplayed
        if (!isCycling) {
            val stepsDisplayed = getStepsFromMeter(distance).toString() + " " + getString(R.string.steps_unit)
            stepsText.text = stepsDisplayed
        }
    }

    private fun resetTracker() {
        tracking = false
        pathPoints = mutableListOf()
        bearing = 0f

        totalDistance = 0
        totalDuration = 0L
        isCycling = false
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty() && map !== null) {
            val zoomLevel = if (map!!.cameraPosition.zoom > MIN_MAP_ZOOM) map!!.cameraPosition.zoom else MAP_ZOOM
            val position = pathPoints.last().last()
            val cameraPosition = CameraPosition.builder()
                .target(position)
                .bearing(bearing)
                .zoom(zoomLevel)
                .build()
            map!!.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                null
            )
        }
    }

    private fun zoomOutWholeRoute() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun saveToDb() {
        map?.snapshot { img ->
            val distance = totalDistance
            val duration = totalDuration
            val speed = getAvgSpeed(distance, duration)
            val date = Date(Calendar.getInstance().timeInMillis)
            val steps = if (!isCycling) getStepsFromMeter(distance) else null
            val track = Track(date, duration, distance, steps, speed, img)
            Log.d(TAG, track.toString())
            Snackbar.make(
                requireActivity().findViewById(R.id.fragmentMain),
                "Track saved successfully!",
                Snackbar.LENGTH_LONG
            ).show()
            trackViewModel.insert(track).observe(viewLifecycleOwner, {
                val bundle = Bundle()
                bundle.putInt("trackId", it)
                findNavController().navigate(R.id.fragment_track_viewer, bundle)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        resetTracker()
        updateUiData()
        mapView?.onStart()
        checkPermission()
    }

    /* MAP LIFECYCLE */
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    /* TRACKING SERVICE FUNCTIONS */
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    /* PERMISSION FUNCTIONS */
    private fun checkPermission() {
        if (locationPermissionApproved(requireContext())) {
            Log.d(TAG, "Permission Granted")
        } else {
            requestLocationPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        if (
                grantResults.isEmpty() ||
                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                        PackageManager.PERMISSION_DENIED))
        {
            Snackbar.make(
                requireActivity().findViewById(R.id.fragmentMain),
                R.string.permission_denied_explanation,
                5 * 1000 // 5 seconds
            )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
        } else {
            // start trackerr
            Log.d(TAG, "Permission Granted")
        }
    }

    @TargetApi(29)
    private fun requestLocationPermissions() {
        if (locationPermissionApproved(requireContext()))
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                Log.d(TAG, "Request foreground and background location permission")
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> {
                Log.d(TAG, "Request foreground only location permission")
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }
        }
        requestPermissions(
            permissionsArray,
            resultCode
        )
    }
}

private const val TAG = "TrackerFragment"