package com.saraga.workoutapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(
    entities = [Track::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase() {

    abstract fun trackDAO(): TrackDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TrackDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): TrackDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "track_database"
                ).addCallback(TrackDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class TrackDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.trackDAO())
                }
            }
        }

        suspend fun populateDatabase(trackDao: TrackDAO) {
            // Delete all content here.
            trackDao.deleteAll()

            // Add sample tracks.
            var track = Track(
                Date(2021, 4, 25),
                60 * 60 * 1000, // 1 hour, data in millis
                4500, // in meters
                6000, // ini ngasal hehe
                4f, // ngasal jg, m/s harusnya
                null // gaada image
            )
            trackDao.insert(track)
            track = Track(
                Date(2021, 4, 26),
                60 * 10 * 1000, // 10 menit
                2400, // in meters
                2500, // ini ngasal hehe
                12f, // ngasal jg, m/s harusnya
                null // gaada image
            )
            trackDao.insert(track)
        }
    }
}