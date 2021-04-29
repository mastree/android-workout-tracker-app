package com.saraga.workoutapp.sportsnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saraga.workoutapp.data.News
import com.saraga.workoutapp.repository.NewsRepository
import kotlinx.coroutines.launch

class SportsViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {
    val myResponse: MutableLiveData<List<News>> = MutableLiveData()

    fun getNews(){
        viewModelScope.launch {
            val response = newsRepository.getNews()
            myResponse.value = response
        }
    }
}