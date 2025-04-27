package com.example.mapio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.BusStop

class ScheduleDetailAdapter(
    private val stops: List<BusStop>,
    private val schedule: Map<String, List<Int>>
) : RecyclerView.Adapter<ScheduleDetailAdapter.ScheduleDetailViewHolder>() {

    class ScheduleDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopNameTextView: TextView = itemView.findViewById(R.id.stopNameTextView)
        val timesTextView: TextView = itemView.findViewById(R.id.timesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_detail, parent, false)
        return ScheduleDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleDetailViewHolder, position: Int) {
        val stop = stops[position]
        holder.stopNameTextView.text = stop.name

        val times = schedule[stop.id] ?: emptyList()
        if (times.isNotEmpty()) {
            val formattedTimes = times.joinToString("   ") { time ->
                val hours = time / 60
                val minutes = time % 60
                "%02d:%02d".format(hours, minutes)
            }
            holder.timesTextView.text = formattedTimes
        } else {
            holder.timesTextView.text = "No scheduled stops"
        }
    }

    override fun getItemCount(): Int = stops.size
} 