package com.saraga.workoutapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtility {
    companion object{
        @Volatile
        private var INSTANCE: DateUtility? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDateFromStringResponse(resp: String): Date{
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val temp = LocalDateTime.parse(resp, format)
            return Date.from(temp.atZone(ZoneId.systemDefault()).toInstant())
        }
    }
}