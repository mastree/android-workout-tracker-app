package com.saraga.workoutapp

import android.app.Application
import com.saraga.workoutapp.data.MainDatabase
import com.saraga.workoutapp.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { MainDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TrackRepository(database.trackDAO()) }
}