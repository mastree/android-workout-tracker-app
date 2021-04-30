package com.saraga.workoutapp.tracker

import androidx.lifecycle.*
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TrackerViewModel(private val repository: TrackRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

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
