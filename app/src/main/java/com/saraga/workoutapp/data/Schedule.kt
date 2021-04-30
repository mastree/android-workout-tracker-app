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
    var endClock: Date = Date(0)
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
}