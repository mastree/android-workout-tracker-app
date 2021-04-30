package com.saraga.workoutapp.viewer

import androidx.lifecycle.*
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewerViewModel(private val repository: TrackRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    fun findTrackById(id: Int): LiveData<Track> = repository.findById(id)
}

class ViewerViewModelFactory(private val repository: TrackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
