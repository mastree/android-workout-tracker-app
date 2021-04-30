package com.saraga.workoutapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("DELETE FROM schedule_table")
    suspend fun deleteAll()

    @Query(value = "SELECT * FROM schedule_table")
    fun getAllSchedules(): LiveData<List<Schedule>>

    @Query(value = "DELETE FROM schedule_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}