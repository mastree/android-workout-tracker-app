package com.saraga.workoutapp.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.saraga.workoutapp.R
import com.saraga.workoutapp.utils.DateUtility
import java.util.*

class TrainingHistory : Fragment() {
    private var picked: Date = Calendar.getInstance().time
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training_history, container, false)

        val cvHistory = view.findViewById<CalendarView>(R.id.calHistory)
        val btnOpen = view.findViewById<AppCompatButton>(R.id.btnPickCalenderHistory)

        cvHistory.setOnDateChangeListener { cview, year, month, dayOfMonth ->
            var c = Calendar.getInstance()
            c.set(year, month, dayOfMonth)
            picked = c.time
        }
        btnOpen.setOnClickListener {
            val temp = Bundle()
            temp.putLong("date", picked.time)
            findNavController().navigate(R.id.historyListFragment, temp)
        }

        return view
    }
}