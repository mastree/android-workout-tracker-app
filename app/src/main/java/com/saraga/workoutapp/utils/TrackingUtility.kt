package com.saraga.workoutapp.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.saraga.workoutapp.services.Polyline
import com.saraga.workoutapp.utils.Constants.Companion.STEPS_TO_METER
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.round

object TrackingUtility {
    val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    @TargetApi(29)
    fun locationPermissionApproved(context: Context): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    fun getFormattedTimestamp(millis: Long, includeMillis: Boolean = false): String {
        var ms = millis
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        ms -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        ms -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms)
        ms -= TimeUnit.SECONDS.toMillis(seconds)
        val milliseconds = ms/10
        var formattedStr = "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
        if (includeMillis) {
            formattedStr += ":${if (milliseconds < 10) "0" else ""}$milliseconds"
        }
        return formattedStr
    }

    fun getStepsFromMeter(meter: Int): Int {
        return ceil(meter / STEPS_TO_METER).toInt()
    }

    fun getAvgSpeed(distance: Int, duration: Long): Float {
        if (distance == 0 && duration == 0L)
            return 0f
        return round((distance / 1000f) / (duration / 1000f / 60 / 60) * 10 ) / 10f
    }

    fun calculatePolylineDistance(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude, pos1.longitude,
                pos2.latitude, pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}