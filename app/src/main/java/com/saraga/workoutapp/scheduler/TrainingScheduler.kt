package com.saraga.workoutapp.scheduler

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.MainDatabase
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.repository.ScheduleRepository
import com.saraga.workoutapp.sportsnews.NewsAdapter
import com.saraga.workoutapp.utils.DateUtility
import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.coroutines.coroutineContext

class TrainingScheduler : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val viewModel: SchedulerViewModel by viewModels {
        SchedulerViewModelFactory((requireActivity().application as MainApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training_scheduler, container, false)

        mRecyclerView = view.findViewById(R.id.rvSchedule)

        val gridLayoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = gridLayoutManager
        scheduleAdapter = ScheduleAdapter(listOf(), viewModel)
        mRecyclerView.adapter = scheduleAdapter

        viewModel.allSchedule.observe(viewLifecycleOwner, {
            scheduleAdapter = ScheduleAdapter(it, viewModel)
            mRecyclerView.adapter = scheduleAdapter
        })

        val fabSchedule = view.findViewById<FloatingActionButton>(R.id.fabSchedule)
        fabSchedule.setOnClickListener {
            findNavController().navigate(R.id.addScheduleFragment)
        }

        return view
    }
}