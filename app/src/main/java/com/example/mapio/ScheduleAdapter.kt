package com.example.mapio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.BusRoute
import com.example.mapio.data.BusStop

class ScheduleAdapter(
    private val routes: List<BusRoute>,
    private val stops: List<BusStop>,
    private val onRouteClick: (BusRoute) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    
    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeNameTextView: TextView = itemView.findViewById(R.id.routeNameTextView)
        val scheduleTimeTextView: TextView = itemView.findViewById(R.id.scheduleTimeTextView)
        val scheduleDetailsTextView: TextView = itemView.findViewById(R.id.scheduleDetailsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val route = routes[position]
        
        // Set route name
        holder.routeNameTextView.text = route.name
        
        // Get first and last stop names
        val firstStopId = route.stops.firstOrNull()
        val lastStopId = route.stops.lastOrNull()
        val firstStop = stops.find { it.id == firstStopId }?.name ?: "Unknown"
        val lastStop = stops.find { it.id == lastStopId }?.name ?: "Unknown"
        
        // Format route endpoints
        holder.scheduleTimeTextView.text = "$firstStop → $lastStop"
        
        // Format schedule times for the first stop
        val scheduleDetails = firstStopId?.let { stopId ->
            route.schedule[stopId]?.take(3)?.joinToString(", ") { time ->
                val hours = time / 60
                val minutes = time % 60
                "%02d:%02d".format(hours, minutes)
            }
        } ?: "No schedule available"
        
        holder.scheduleDetailsTextView.text = "First departures: $scheduleDetails"
        
        // Set click listener
        holder.itemView.setOnClickListener { onRouteClick(route) }
    }

    override fun getItemCount(): Int = routes.size
} 