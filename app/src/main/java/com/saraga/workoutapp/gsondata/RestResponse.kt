package com.saraga.workoutapp.gsondata

data class RestResponse(
    var status: String?,
    var totalResults: Double?,
    var articles: List<Article>?
)