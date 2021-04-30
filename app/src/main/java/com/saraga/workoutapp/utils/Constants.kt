package com.saraga.workoutapp.utils

import java.util.*

class Constants {
    companion object{
        const val BASE_URL = "https://newsapi.org"
        const val STEP_PER_METER = 1.3123
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

    }
}