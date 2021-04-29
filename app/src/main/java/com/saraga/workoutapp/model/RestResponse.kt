package com.saraga.workoutapp.model

data class RestResponse(
    var status: String?,
    var totalResults: Double?,
    var articles: List<Article>?
)