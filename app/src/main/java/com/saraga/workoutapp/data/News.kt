package com.saraga.workoutapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.saraga.workoutapp.model.Article
import com.saraga.workoutapp.utils.DateUtility
import java.util.*

data class News(
    var source: String = "",
    var author: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var urlToImage: String = "",
    var publishedAt: Date = Date(0),
    var content: String = ""
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun setNews(article: Article){
        if (article.source != null){
            if (article.source!!.name != null){
                this.source = article.source!!.name!!
            }
        }
        if (article.author != null){
            this.author = article.author!!
        }
        if (article.title != null){
            this.title = article.title!!
        }
        if (article.description != null){
            this.description = article.description!!
        }
        if (article.url != null){
            this.url = article.url!!
        }
        if (article.urlToImage != null){
            this.urlToImage = article.urlToImage!!
        }
        if (article.publishedAt != null){
            this.publishedAt = DateUtility.getDateFromStringResponse(article.publishedAt!!)
        }
        if (article.content != null){
            this.content = article.content!!
        }
    }
}