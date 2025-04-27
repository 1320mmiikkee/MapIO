package com.example.mapio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.TransportDatabase
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {
    private lateinit var database: TransportDatabase
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = TransportDatabase.getDatabase(requireContext())
        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadScheduleData()
    }

    private fun loadScheduleData() {
        lifecycleScope.launch {
            val routes = database.transportDao().getAllBusRoutes()
            val stops = database.transportDao().getAllBusStops()
            recyclerView.adapter = ScheduleAdapter(routes, stops) { route ->
                openScheduleDetail(route.name)
            }
        }
    }

    private fun openScheduleDetail(routeName: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ScheduleDetailFragment.newInstance(routeName)
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }
} 