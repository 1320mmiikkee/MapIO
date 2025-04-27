package com.example.mapio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.NicosiaBusRoutes

class StopsFragment : Fragment() {
    interface OnStopSelectedListener {
        fun onStopSelected(stop: com.example.mapio.data.BusStop)
    }
    private lateinit var recyclerView: RecyclerView
    private var listener: OnStopSelectedListener? = null

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        listener = context as? OnStopSelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stops, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.stopsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadStops()
    }

    private fun loadStops() {
        recyclerView.adapter = StopsAdapter(NicosiaBusRoutes.STOPS) { stop ->
            listener?.onStopSelected(stop)
        }
    }

    companion object {
        fun newInstance() = StopsFragment()
    }
} 