package com.example.mapio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.BusStop

class StopsAdapter(
    private val stops: List<BusStop>,
    private val onStopClick: (BusStop) -> Unit
) : RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {
    class StopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopNameTextView: TextView = itemView.findViewById(R.id.stopNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = stops[position]
        holder.stopNameTextView.text = stop.name
        holder.itemView.setOnClickListener { onStopClick(stop) }
    }

    override fun getItemCount(): Int = stops.size
} 