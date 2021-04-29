package com.saraga.workoutapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.data.TrackDAO

class TrackRepository(private val trackDao: TrackDAO) {

    val allTracks: LiveData<List<Track>> = trackDao.getAllRunsSortedByDate()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(track: Track) {
        trackDao.insert(track)
    }
}