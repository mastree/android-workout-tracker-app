package com.saraga.workoutapp.tracker

import androidx.lifecycle.*
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class TrackerViewModel(private val repository: TrackRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTracks: LiveData<List<Track>> = repository.getAllTracksSortedByDate()
    var lbound: Long = 0
    var rbound: Long = 0
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(track: Track): LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
            val id = repository.insert(track)
            result.postValue(id.toInt())
        }
        return result
    }
    fun tracksBoundedByDate(date: Date): LiveData<List<Track>>{
        val c = Calendar.getInstance()
        c.time = date
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        lbound = c.time.time
        c.add(Calendar.DATE, 1)
        rbound = c.time.time
        return repository.getAllTracksBoundedByDate(lbound, rbound)
    }
}

class TrackerViewModelFactory(private val repository: TrackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
