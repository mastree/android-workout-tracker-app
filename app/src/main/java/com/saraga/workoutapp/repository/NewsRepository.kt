package com.saraga.workoutapp.repository

import com.saraga.workoutapp.api.NewsApiSingleton
import com.saraga.workoutapp.data.News

class NewsRepository {
    suspend fun getNews(): List<News>{
        var newslist: MutableList<News> = mutableListOf()
        val data = NewsApiSingleton.api.getNews()
        if (data.articles != null){
            for (item in data.articles!!){
                var news = News()
                news.setNews(item)
                newslist.add(news)
            }
        }
        return newslist
    }
}