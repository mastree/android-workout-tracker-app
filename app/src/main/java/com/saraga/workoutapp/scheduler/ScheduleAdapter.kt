package com.saraga.workoutapp.scheduler

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
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.sportsnews.NewsAdapter
import com.saraga.workoutapp.utils.DateUtility
import com.squareup.picasso.Picasso
import java.lang.IllegalArgumentException

class ScheduleAdapter(
        private val schedules: List<Schedule>
): RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ScheduleViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(
                R.layout.grid_schedule,
                parent,
                false
        )
        return ScheduleAdapter.ScheduleViewHolder(view);
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.ScheduleViewHolder, position: Int) {
        val curSchedule = schedules[position]
        holder.itemView.apply {
            val ivImage = findViewById<ImageView>(R.id.ivImageSchedule)
            val tvStart = findViewById<TextView>(R.id.tvStartClockSchedule)
            val tvEnd = findViewById<TextView>(R.id.tvEndClockSchedule)
            val tvDay = findViewById<TextView>(R.id.tvDaySchedule)

            if (!curSchedule.isRun) ivImage.setImageResource(R.drawable.ic_biking)
            tvStart.text = DateUtility.getClockString(curSchedule.beginClock) //curSchedule.beginClock.hours.toString() + ":" + curSchedule.beginClock.minutes.toString() + ":" + curSchedule.beginClock.seconds.toString()
            tvEnd.text = DateUtility.getClockString(curSchedule.endClock) //curSchedule.endClock.hours.toString() + ":" + curSchedule.endClock.minutes.toString() + ":" + curSchedule.endClock.seconds.toString()
            tvDay.text = curSchedule.getActiveDay()
        }
    }

    override fun getItemCount(): Int {
        return schedules.size
    }
}