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
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.saraga.workoutapp.BuildConfig
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.services.Polyline
import com.saraga.workoutapp.services.TrackingService
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.saraga.workoutapp.utils.Constants.Companion.BACKGROUND_LOCATION_PERMISSION_INDEX
import com.saraga.workoutapp.utils.Constants.Companion.LOCATION_PERMISSION_INDEX
import com.saraga.workoutapp.utils.Constants.Companion.MAP_ZOOM
import com.saraga.workoutapp.utils.Constants.Companion.POLYLINE_COLOR
import com.saraga.workoutapp.utils.Constants.Companion.POLYLINE_WIDTH
import com.saraga.workoutapp.utils.Constants.Companion.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.saraga.workoutapp.utils.Constants.Companion.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.saraga.workoutapp.utils.TrackingUtility.locationPermissionApproved
import com.saraga.workoutapp.utils.TrackingUtility.runningQOrLater
import kotlinx.android.synthetic.main.fragment_training_tracker.*

class Tracker() : Fragment() {
    private val trackViewModel: TrackerViewModel by viewModels {
        TrackerViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    private var map: GoogleMap? = null
    private var tracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var bearing = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }


        subscribeToObservers()
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
        })
        TrackingService.bearing.observe(viewLifecycleOwner, {
            bearing = it
        })
    }

    private fun toggleRun() {
        if (tracking){
            sendCommandToService(ACTION_STOP_SERVICE)
        } else {
            sendCommandToService(ACTION_START_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        tracking = isTracking
        if (tracking) {
            startStopButton.text = getString(R.string.stop)
        } else {
            startStopButton.text = getString(R.string.start)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty() && map !== null) {
            val zoomLevel = if (map!!.cameraPosition.zoom != map!!.minZoomLevel) map!!.cameraPosition.zoom else MAP_ZOOM
            Log.d(TAG, "Zoom Level: $zoomLevel")
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

    override fun onStart() {
        super.onStart()
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