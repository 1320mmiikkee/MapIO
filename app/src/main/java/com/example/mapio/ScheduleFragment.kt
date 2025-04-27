package com.example.mapio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.NicosiaBusRoutes

class ScheduleFragment : Fragment() {
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
        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadScheduleData()
    }

    private fun loadScheduleData() {
        recyclerView.adapter = ScheduleAdapter(NicosiaBusRoutes.ROUTES, NicosiaBusRoutes.STOPS) { route ->
            openScheduleDetail(route.name)
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