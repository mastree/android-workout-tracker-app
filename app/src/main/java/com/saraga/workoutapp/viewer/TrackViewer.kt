package com.saraga.workoutapp.viewer

import android.app.AlarmManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.tracker.TrackerViewModel
import com.saraga.workoutapp.tracker.TrackerViewModelFactory
import com.saraga.workoutapp.utils.DateUtility
import com.saraga.workoutapp.utils.TrackingUtility
import kotlinx.android.synthetic.main.fragment_track_viewer.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.round

class TrackViewer : Fragment() {
    private val viewerViewModel: ViewerViewModel by viewModels {
        ViewerViewModelFactory((requireActivity().application as MainApplication).repository)
    }
    private var trackId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trackId = it.getInt("trackId", 0)
            Log.d(TAG, "Get Track Id: $trackId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewerViewModel.findTrackById(trackId!!).observe(viewLifecycleOwner, {
            Log.d(TAG, it.toString())
            updateUiData(it)
        })
    }

    private fun updateUiData(track: Track) {
        val dateTimeStart = Date(track.date.time - track.duration) // karena value dari date dipanggil setelah tracking selesai
        val dateText = DateUtility.getDateString(dateTimeStart)
        val timeStartText = DateUtility.getClockString(dateTimeStart)
        val timeEndText = DateUtility.getClockString(track.date)
        val timeText = "$timeStartText - $timeEndText"
        val durationText = TrackingUtility.getFormattedTimestamp(track.duration, true)
        val distanceText = (round(track.distance / 10f) / 100f).toString() + " " + getString(R.string.viewer_dist_unit)
        val speedText = TrackingUtility.getAvgSpeed(track.distance, track.duration).toString() + " " + getString(R.string.speed_unit)
        val isCycling = track.steps == null

        tvViewerDistance.text = distanceText
        tvViewerDate.text = dateText
        tvViewerTime.text = timeText
        tvViewerDuration.text = durationText
        tvViewerSpeed.text = speedText
        Glide.with(this).load(track.img).into(ivViewerMap)
        if (!isCycling) {
            val stepsText = TrackingUtility.getStepsFromMeter(track.distance).toString() + " " + getString(R.string.steps_unit)
            tvViewerSteps.text = stepsText
            tvViewerSteps.visibility = VISIBLE
            ivViewerType.setImageResource(R.drawable.ic_running)
        } else {
            tvViewerSteps.visibility = GONE
            ivViewerType.setImageResource(R.drawable.ic_biking)
        }
    }
}

private const val TAG = "TrackViewer"