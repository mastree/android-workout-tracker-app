package com.saraga.workoutapp.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saraga.workoutapp.utils.DateUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(
    entities = [Track::class, Schedule::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDatabase : RoomDatabase() {

    abstract fun trackDAO(): TrackDAO
    abstract fun scheduleDAO(): ScheduleDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): MainDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "track_database"
                ).addCallback(TrackDatabaseCallback(scope))
                    .addCallback(ScheduleDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
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

    private class ScheduleDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.scheduleDAO())
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun populateDatabase(scheduleDAO: ScheduleDAO) {
            // Delete all content here.
            scheduleDAO.deleteAll()

            // Add sample tracks.
            var schedule = Schedule(0, true,false, Date(0), true, false, false, false, false, false, false, DateUtility.getDateFromClock("15:00:00"), DateUtility.getDateFromClock("17:00:00"))
            scheduleDAO.insert(schedule)
            schedule = Schedule(0, false,false, Date(0), false, true, false, false, false, false, false, Calendar.getInstance().time, Calendar.getInstance().time)
            scheduleDAO.insert(schedule)
            schedule = Schedule(0, true,false, Date(0), false, true, false, true, false, false, false, Calendar.getInstance().time, Calendar.getInstance().time)
            scheduleDAO.insert(schedule)
        }
    }
}