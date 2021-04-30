package com.saraga.workoutapp.sportsnews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.News
import com.squareup.picasso.Picasso
import java.lang.IllegalArgumentException

class NewsAdapter(
        private val newslist: List<News>,
        private val context: Context
): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(
            R.layout.grid_news,
            parent,
            false
        )
        return NewsViewHolder(view);
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val curNews = newslist[position]
        holder.itemView.apply{
            val ivNews = findViewById<ImageView>(R.id.ivImageNews)
            val tvDate = findViewById<TextView>(R.id.tvDateNews)
            val tvTitle = findViewById<TextView>(R.id.tvTitleNews)
            val tvSource = findViewById<TextView>(R.id.tvSourceNews)
            val cvNews = findViewById<CardView>(R.id.cvNews)

            if (curNews.urlToImage != ""){
                try{
                    Picasso.get().load(curNews.urlToImage).into(ivNews)
                } catch (e: IllegalArgumentException){
                    // DO NOTHING
                }
            }
            tvDate.text = curNews.publishedAt.toString()
            tvTitle.text = curNews.title
            tvSource.text = "source: " + curNews.source

            cvNews.setOnClickListener{
                val temp = Bundle()
                temp.putString("link", curNews.url)
                findNavController().navigate(R.id.newsSiteFragment, temp)
            }
        }
    }

    override fun getItemCount(): Int {
        return newslist.size
    }
}