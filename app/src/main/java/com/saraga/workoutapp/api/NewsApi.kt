package com.saraga.workoutapp.api

import com.saraga.workoutapp.model.RestResponse
import retrofit2.http.GET

interface NewsApi {
    @GET("/v2/top-headlines?country=id&category=sports&apiKey=03db5d571db042c49be77617b2efa3c8")
    suspend fun getNews(): RestResponse
}