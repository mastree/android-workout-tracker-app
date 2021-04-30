package com.saraga.workoutapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track): Long

    @Delete
    suspend fun delete(track: Track)

    @Query("DELETE FROM track_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM track_table WHERE id=:id")
    fun findById(id: Int): LiveData<Track>

    @Query("SELECT * FROM track_table ORDER BY date DESC")
    fun getAllTracksSortedByDate(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY duration DESC")
    fun getAllTracksSortedByDuration(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY speed DESC")
    fun getAllTracksSortedBySpeed(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY distance DESC")
    fun getAllTracksSortedByDistance(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table WHERE :lbound <= (date - duration) AND (date - duration) < :rbound")
    fun getAllTracksBoundedByDate(lbound: Long, rbound: Long): LiveData<List<Track>>
}