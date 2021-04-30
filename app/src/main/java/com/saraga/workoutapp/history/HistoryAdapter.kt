package com.saraga.workoutapp.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.Track
import com.saraga.workoutapp.sportsnews.NewsAdapter
import com.saraga.workoutapp.utils.DateUtility
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.lang.IllegalArgumentException

class HistoryAdapter(
    private val histories: List<Track>
): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.HistoryViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(
            R.layout.grid_history,
            parent,
            false
        )
        return HistoryAdapter.HistoryViewHolder(view);
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
        val curHistory = histories[position]
        holder.itemView.apply {
            val cvHistory = findViewById<CardView>(R.id.cvHistory)
            val ivImageHistory = findViewById<ImageView>(R.id.ivImageHistory)
            val tvStartHistory = findViewById<TextView>(R.id.tvStartHistory)
            val tvEndHistory = findViewById<TextView>(R.id.tvEndHistory)
            val tvDistanceHistory = findViewById<TextView>(R.id.tvDistanceHistory)
            val tvSpeedHistory = findViewById<TextView>(R.id.tvSpeedHistory)

            if (!curHistory.isRunType()){
                ivImageHistory.setImageResource(R.drawable.ic_biking)
                tvDistanceHistory.text = (curHistory.distance.toDouble() / 1000.0).toString() + " KM"
            } else{
                tvDistanceHistory.text = curHistory.steps.toString() + " Steps"
            }
            tvStartHistory.text = DateUtility.getClockString(curHistory.getStart())
            tvEndHistory.text = DateUtility.getClockString(curHistory.getEnd())
            tvSpeedHistory.text = curHistory.speed.toString() + " KM/h"

            cvHistory.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("trackId", curHistory.id!!)
                findNavController().navigate(R.id.fragment_track_viewer, bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return histories.size
    }
}