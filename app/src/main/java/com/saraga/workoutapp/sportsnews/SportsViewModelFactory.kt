package com.saraga.workoutapp.sportsnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saraga.workoutapp.repository.NewsRepository

class SportsViewModelFactory(
    private val newsRepository: NewsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SportsViewModel(newsRepository) as T
    }
}