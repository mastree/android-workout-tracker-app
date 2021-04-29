package com.saraga.workoutapp.utils

class Constants {
    companion object{
        const val BASE_URL = "https://newsapi.org"
        const val DB_NAME = "main_database"

        /* Tracker Permissions Code */
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

        /* Tracker Service Action Code */
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_SHOW_TRACKER_FRAGMENT = "ACTION_SHOW_TRACKER_FRAGMENT"

        /* Tracking Service Channel Code */
        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "tracking"
        const val NOTIFICATION_ID = 33

        /* Tracking Service Other Constants */
        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

    }
}