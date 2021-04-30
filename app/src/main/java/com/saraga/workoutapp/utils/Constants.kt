package com.saraga.workoutapp.utils

import java.util.*
import android.graphics.Color

class Constants {
    companion object{
        const val BASE_URL = "https://newsapi.org"
        const val STEP_TO_METER =  0.762
        val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calendarWeekdays = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
        val dayToCalendarDay = mapOf<String, Int>(
                weekdays[0] to calendarWeekdays[0],
                weekdays[1] to calendarWeekdays[1],
                weekdays[2] to calendarWeekdays[2],
                weekdays[3] to calendarWeekdays[3],
                weekdays[4] to calendarWeekdays[4],
                weekdays[5] to calendarWeekdays[5],
                weekdays[6] to calendarWeekdays[6]
        )

        const val DB_NAME = "main_database"

        /* Tracker Permissions Code */
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

        /* Tracker Service Action Code */
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_SHOW_TRACKER_FRAGMENT = "ACTION_SHOW_TRACKER_FRAGMENT"

        /* Tracking Service Channel Code */
        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "tracking"
        const val NOTIFICATION_ID = 33

        /* Tracking Service Other Constants */
        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

        /* Polyline Options */
        const val POLYLINE_COLOR = Color.RED
        const val POLYLINE_WIDTH = 7f

        /* Map Camera Configurations */
        const val MAP_ZOOM = 16.5f
    }
}