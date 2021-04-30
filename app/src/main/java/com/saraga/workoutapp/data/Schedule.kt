package com.saraga.workoutapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saraga.workoutapp.utils.Constants
import com.saraga.workoutapp.utils.DateUtility
import java.util.*

@Entity(tableName = "schedule_table")
class Schedule(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var isRun: Boolean = true,
    var justOnce: Boolean = false,
    var date: Date = Date(0),
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false,
    var beginClock: Date = Date(0),
    var endClock: Date = Date(0),
    var target: Double = 1.0 // in meters
){
    fun getActiveDay(): String{
        var ret: String = ""
        if (justOnce){
            ret = DateUtility.getDateString(date)
        } else{
            val temp = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
            var allTrue = true;
            for (i in 0..temp.size - 1){
                if (temp[i]){
                    if (ret.isEmpty()){
                        ret = ret + Constants.weekdays[i]
                    } else{
                        ret = ret + ", " + Constants.weekdays[i]
                    }
                } else allTrue = false
            }
            if (allTrue) ret = "Everyday"
        }
        return ret
    }

    fun nextExist(): Boolean{
        if (justOnce) return false
        return true
    }

    fun getNext(): Date{
        if (justOnce) return date

        val listDayBool = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
        val pos = Constants.calendarWeekdays.indexOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

        var c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, DateUtility.getElement(beginClock, Calendar.HOUR_OF_DAY))
        c.set(Calendar.MINUTE, DateUtility.getElement(beginClock, Calendar.MINUTE))

        for (i in 0..6){
            val id = (pos + i) % 7
            if (listDayBool[id]){
                if (!c.before(Calendar.getInstance())) return c.time
                c.add(Calendar.DATE, 1)
            }
        }
        return Date(0)
    }
}