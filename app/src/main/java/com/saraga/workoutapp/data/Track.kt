package com.saraga.workoutapp.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "track_table")
data class Track(
    var date: Date = Date(0),
    var duration: Long = 0L,
    var distance: Int = 0,
    var steps: Int? = null,
    var speed: Float = 0f,
    var img: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun getStart(): Date {
        return Date(date.time - duration)
    }

    fun getEnd(): Date {
        return date
    }

    fun isRunType(): Boolean {
        return steps != null
    }
}