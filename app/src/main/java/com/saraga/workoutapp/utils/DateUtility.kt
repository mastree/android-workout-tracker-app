package com.saraga.workoutapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
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

        fun getDateString(date: Date): String{
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            return formatter.format(date)
        }

        fun getClockString(date: Date, withSecond: Boolean = false): String{
            var pattern: String = "HH:mm"
            if (withSecond) pattern = pattern + ":ss"
            val formatter = SimpleDateFormat(pattern)
            return formatter.format(date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDateFromClock(clock: String): Date{
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val nclock = "2020-01-01T" + clock
            val temp = LocalDateTime.parse(nclock, format)
            return Date.from(temp.atZone(ZoneId.systemDefault()).toInstant())
        }
    }
}