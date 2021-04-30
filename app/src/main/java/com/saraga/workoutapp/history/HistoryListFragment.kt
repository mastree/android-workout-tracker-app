package com.saraga.workoutapp.history

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.scheduler.ScheduleAdapter
import com.saraga.workoutapp.scheduler.SchedulerViewModelFactory
import com.saraga.workoutapp.sportsnews.NewsAdapter
import com.saraga.workoutapp.sportsnews.SportsViewModel
import com.saraga.workoutapp.tracker.TrackerViewModel
import com.saraga.workoutapp.tracker.TrackerViewModelFactory
import com.saraga.workoutapp.utils.DateUtility
import java.util.*

class HistoryListFragment : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var date: Date
    private val viewModel: TrackerViewModel by viewModels {
        TrackerViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val temp = it.getLong("date", 0)
            date = Date(temp)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_list, container, false)

        val tvDate = view.findViewById<TextView>(R.id.tvDateHistory)
        tvDate.text = DateUtility.getDateString(date)

        mRecyclerView = view.findViewById(R.id.rvHistory)
        val gridLayoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = gridLayoutManager
        historyAdapter = HistoryAdapter(listOf())
        mRecyclerView.adapter = historyAdapter

//        viewModel.setDateForTracks(date)
        Log.d("range", viewModel.lbound.toString() + " " + viewModel.rbound.toString())
        viewModel.tracksBoundedByDate(date).observe(viewLifecycleOwner, {
            historyAdapter = HistoryAdapter(it)
            mRecyclerView.adapter = historyAdapter
        })

        return view
    }
}