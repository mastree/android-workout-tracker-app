package com.saraga.workoutapp

//import com.android.volley.Request
//import com.android.volley.RequestQueue
//import com.android.volley.Response
//import com.android.volley.toolbox.JsonObjectRequest
//import com.android.volley.toolbox.Volley
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.saraga.workoutapp.data.News
import com.saraga.workoutapp.gsondata.RestResponse
import okhttp3.*
import java.io.IOException

class SportsNews() : Fragment() {
    private val RESTURL = "https://newsapi.org/v2/top-headlines?country=id&category=sports&apiKey=03db5d571db042c49be77617b2efa3c8"
    private lateinit var data: RestResponse
    private var newslist: MutableList<News> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sports_news, container, false)
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(RESTURL)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful()) {
                    var myResponse: String = response.body()!!.string()
                    activity!!.runOnUiThread(Runnable {
//                        Log.println(Log.DEBUG, "restResponse", myResponse)
//                        Log.println(Log.DEBUG, "restResponse", response.body()!!.string())
                        data = Gson().fromJson(myResponse, RestResponse::class.java)
//                        Log.println(Log.DEBUG, "restResponse in kotlin", data.toString())
                        loadNews()
                    })
                }
            }
        })

//        val tvSportsNews = view.findViewById<TextView>(R.id.tvSportsNews)
//        tvSportsNews.setOnClickListener {
//            val temp = Bundle()
//            temp.putString("link", "https://www.itb.ac.id")
//            findNavController().navigate(R.id.newsSiteFragment, temp)
//        }
        return view
    }

    fun loadNews(){
        if (data.articles != null){
            newslist = mutableListOf()
            for (item in data.articles!!){
                var news = News()
                news.setNews(item)
                newslist.add(news)
            }
//            for (item in newslist){
//                Log.println(Log.DEBUG, "newsUpdate", item.toString())
//            }
        }
    }
}