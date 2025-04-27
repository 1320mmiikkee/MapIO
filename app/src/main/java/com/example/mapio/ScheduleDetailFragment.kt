package com.example.mapio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.BusStop
import com.example.mapio.data.TransportDatabase
import kotlinx.coroutines.launch

class ScheduleDetailFragment : Fragment() {
    private var routeName: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeName = arguments?.getString(ARG_ROUTE_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        titleTextView = view.findViewById(R.id.routeTitleTextView)
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        loadScheduleData()
    }

    private fun loadScheduleData() {
        lifecycleScope.launch {
            val database = TransportDatabase.getDatabase(requireContext())
            val routes = database.transportDao().getAllBusRoutes()
            val route = routes.find { it.name == routeName }
            
            if (route != null) {
                // Set title
                titleTextView.text = route.name
                
                // Get stops for this route
                val allStops = database.transportDao().getAllBusStops()
                val routeStops = route.stops.mapNotNull { stopId ->
                    allStops.find { it.id == stopId }
                }
                
                // Create adapter with stops and schedule
                recyclerView.adapter = ScheduleDetailAdapter(routeStops, route.schedule)
            }
        }
    }

    companion object {
        private const val ARG_ROUTE_NAME = "route_name"
        
        fun newInstance(routeName: String): ScheduleDetailFragment {
            val fragment = ScheduleDetailFragment()
            val args = Bundle()
            args.putString(ARG_ROUTE_NAME, routeName)
            fragment.arguments = args
            return fragment
        }
    }
} 