package com.saraga.workoutapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.data.ScheduleDAO
import com.saraga.workoutapp.data.Track

class ScheduleRepository(private val scheduleDAO: ScheduleDAO) {
    val allSchedule: LiveData<List<Schedule>> = scheduleDAO.getAllSchedules()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(schedule: Schedule) {
        scheduleDAO.insert(schedule)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(schedule: Schedule) {
        scheduleDAO.delete(schedule)
    }
}