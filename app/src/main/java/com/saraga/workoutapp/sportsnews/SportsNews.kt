package com.saraga.workoutapp.sportsnews

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saraga.workoutapp.R
import com.saraga.workoutapp.repository.NewsRepository

class SportsNews() : Fragment() {
    private lateinit var viewModel: SportsViewModel
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sports_news, container, false)

        mRecyclerView = view.findViewById(R.id.rvSportsNews)

        var spanCnt = 1
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCnt = 2
        }
        val gridLayoutManager = GridLayoutManager(context, spanCnt, GridLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = gridLayoutManager
        newsAdapter = NewsAdapter(listOf(), requireContext())
        mRecyclerView.adapter = newsAdapter

        val repository = NewsRepository()
        val viewModelFactory = SportsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SportsViewModel::class.java)
        viewModel.getNews()
        viewModel.myResponse.observe(viewLifecycleOwner, Observer { response ->
            newsAdapter = NewsAdapter(response, requireContext())
            mRecyclerView.adapter = newsAdapter
        })

        return view
    }
}