package com.example.mapio

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mapio.data.BusStop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapio.data.BusRoute
import com.example.mapio.data.NicosiaBusRoutes
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import com.google.android.gms.maps.model.PolylineOptions
import android.app.ProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive

class RoutesFragment : Fragment(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var selectedStop: BusStop? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val nicosiaLatLng = LatLng(35.1729, 33.3636)
    private var mapReady = false
    private var pendingStop: BusStop? = null
    private lateinit var routesRecyclerView: RecyclerView
    private lateinit var routeAdapter: RouteAdapter
    private var progressDialog: ProgressDialog? = null
    private var currentRouteJob: kotlinx.coroutines.Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            selectedStop = it.getParcelable("selected_stop")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        var mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit()
        }
        mapFragment.getMapAsync(this)

        // Setup routes RecyclerView
        routesRecyclerView = view.findViewById(R.id.routesRecyclerView)
        routesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        routeAdapter = RouteAdapter(NicosiaBusRoutes.ROUTES) { route ->
            onRouteClicked(route)
        }
        routesRecyclerView.adapter = routeAdapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapReady = true
        if (pendingStop != null) {
            showStopOnMap(pendingStop!!)
            pendingStop = null
        } else if (selectedStop != null) {
            showStopOnMap(selectedStop!!)
        } else {
            showUserLocation()
        }
    }

    fun showStopOnMap(stop: BusStop) {
        if (!mapReady || map == null) {
            pendingStop = stop
            return
        }
        
        map?.clear()
        val latLng = LatLng(stop.latitude, stop.longitude)
        map?.addMarker(MarkerOptions().position(latLng).title(stop.name))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        selectedStop = stop
    }

    private fun showUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(nicosiaLatLng, 15f))
                }
            }.addOnFailureListener {
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(nicosiaLatLng, 15f))
            }
        } else {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(nicosiaLatLng, 15f))
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun onRouteClicked(route: BusRoute) {
        // Cancel any ongoing coroutine
        currentRouteJob?.cancel()
        
        val stops = route.stops.mapNotNull { stopId ->
            NicosiaBusRoutes.STOPS.find { it.id == stopId }
        }
        if (stops.size < 2) {
            Toast.makeText(requireContext(), "Not enough stops to draw route", Toast.LENGTH_SHORT).show()
            return
        }
        drawRouteWithDirections(stops)
    }

    private fun drawRouteWithDirections(stops: List<BusStop>) {
        if (!mapReady || map == null) return
        
        map?.clear()
        showLoading(true)
        
        currentRouteJob = CoroutineScope(Dispatchers.IO + Job()).launch {
            try {
                val pathPoints = mutableListOf<LatLng>()
                for (i in 0 until stops.size - 1) {
                    if (!isActive) {
                        return@launch
                    }
                    
                    val origin = stops[i]
                    val dest = stops[i + 1]
                    val url = getDirectionsUrl(origin, dest)
                    val result = URL(url).readText()
                    val points = parsePolyline(result)
                    pathPoints.addAll(points)
                }
                
                withContext(Dispatchers.Main) {
                    if (!isActive) return@withContext
                    
                    showLoading(false)
                    if (pathPoints.isNotEmpty()) {
                        map?.addPolyline(
                            PolylineOptions()
                                .addAll(pathPoints)
                                .color(android.graphics.Color.RED)
                                .width(8f)
                        )
                        
                        // Add markers for stops
                        stops.forEach { stop ->
                            map?.addMarker(
                                MarkerOptions()
                                    .position(LatLng(stop.latitude, stop.longitude))
                                    .title(stop.name)
                            )
                        }
                        
                        // Animate camera to fit the route
                        val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.Builder()
                        pathPoints.forEach { boundsBuilder.include(it) }
                        stops.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
                        val bounds = boundsBuilder.build()
                        val padding = 120 // px
                        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                    } else {
                        Toast.makeText(requireContext(), "Could not fetch route", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                if (isActive) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Error fetching route: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            progressDialog = ProgressDialog(context).apply {
                setMessage("Loading route...")
                setCancelable(false)
                show()
            }
        } else {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    private fun getDirectionsUrl(origin: BusStop, dest: BusStop): String {
        val originStr = "${origin.latitude},${origin.longitude}"
        val destStr = "${dest.latitude},${dest.longitude}"
        val apiKey = getString(R.string.google_maps_key)
        return "https://maps.googleapis.com/maps/api/directions/json?origin=$originStr&destination=$destStr&mode=transit&key=$apiKey"
    }

    private fun parsePolyline(json: String): List<LatLng> {
        val result = mutableListOf<LatLng>()
        val jsonObj = JSONObject(json)
        val routes = jsonObj.getJSONArray("routes")
        if (routes.length() == 0) return result
        val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
        val points = overviewPolyline.getString("points")
        result.addAll(decodePoly(points))
        return result
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    companion object {
        fun newInstance(stop: BusStop? = null): RoutesFragment {
            val fragment = RoutesFragment()
            if (stop != null) {
                val args = Bundle()
                args.putParcelable("selected_stop", stop)
                fragment.arguments = args
            }
            return fragment
        }
    }
}

class RouteAdapter(
    private val routes: List<BusRoute>,
    private val onRouteClick: (BusRoute) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeNameTextView: android.widget.TextView = itemView.findViewById(R.id.routeNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.routeNameTextView.text = route.name
        holder.itemView.setOnClickListener { onRouteClick(route) }
    }

    override fun getItemCount(): Int = routes.size
} 