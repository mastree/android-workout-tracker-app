package com.saraga.workoutapp.scheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.repository.ScheduleRepository
import com.saraga.workoutapp.repository.TrackRepository
import kotlinx.coroutines.launch

class SchedulerViewModel(private val repository: ScheduleRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSchedule: LiveData<List<Schedule>> = repository.allSchedule

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(schedule: Schedule) = viewModelScope.launch {
        repository.insert(schedule)
    }
    fun delete(schedule: Schedule) = viewModelScope.launch {
        repository.delete(schedule)
    }
}

class SchedulerViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchedulerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}