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

        fun getHour(date: Date): Int{
            var cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.HOUR_OF_DAY]
        }
        fun getMinute(date: Date): Int{
            var cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.MINUTE]
        }
        fun getSecond(date: Date): Int{
            var cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.SECOND]
        }
        fun getElement(date: Date, id: Int): Int{
            var cal = Calendar.getInstance()
            cal.time = date
            return cal[id]
        }
    }
}