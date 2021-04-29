package com.saraga.workoutapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)

    @Delete
    suspend fun delete(track: Track)

    @Query("DELETE FROM track_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM track_table ORDER BY date DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY duration DESC")
    fun getAllRunsSortedByDuration(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY speed DESC")
    fun getAllRunsSortedBySpeed(): LiveData<List<Track>>

    @Query("SELECT * FROM track_table ORDER BY distance DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Track>>
}