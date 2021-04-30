package com.saraga.workoutapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Query
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.data.TrackDAO

class TrackRepository(private val trackDao: TrackDAO) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(track: Track): Long {
        return trackDao.insert(track)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(track: Track) {
        trackDao.delete(track)
    }

    fun findById(id: Int): LiveData<Track> = trackDao.findById(id)

    fun getAllTracksSortedByDate(): LiveData<List<Track>> = trackDao.getAllTracksSortedByDate()

    fun getAllTracksSortedByDuration(): LiveData<List<Track>> = trackDao.getAllTracksSortedByDuration()

    fun getAllTracksSortedBySpeed(): LiveData<List<Track>> = trackDao.getAllTracksSortedBySpeed()

    fun getAllTracksSortedByDistance(): LiveData<List<Track>> = trackDao.getAllTracksSortedByDistance()

    fun getAllTracksBoundedByDate(lbound: Long, rbound: Long): LiveData<List<Track>> = trackDao.getAllTracksBoundedByDate(lbound, rbound)
}