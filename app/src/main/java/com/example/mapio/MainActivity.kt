package com.example.mapio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mapio.data.BusStop
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), StopsFragment.OnStopSelectedListener {
    private var selectedStop: BusStop? = null
    private var routesFragment: RoutesFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_routes -> {
                    if (routesFragment == null) {
                        routesFragment = RoutesFragment.newInstance()
                    }
                    loadFragment(routesFragment!!)
                    true
                }
                R.id.navigation_stops -> {
                    loadFragment(StopsFragment.newInstance())
                    true
                }
                R.id.navigation_schedule -> {
                    loadFragment(ScheduleFragment.newInstance())
                    true
                }
                R.id.navigation_notifications -> {
                    loadFragment(NotificationsFragment.newInstance())
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            routesFragment = RoutesFragment.newInstance()
            loadFragment(routesFragment!!)
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    override fun onStopSelected(stop: BusStop) {
        selectedStop = stop
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is RoutesFragment) {
            currentFragment.showStopOnMap(stop)
        } else {
            if (routesFragment == null) {
                routesFragment = RoutesFragment.newInstance()
            }
            loadFragment(routesFragment!!)
            // Post to the main thread to ensure the fragment is attached
            routesFragment?.let { frag ->
                window.decorView.postDelayed({ frag.showStopOnMap(stop) }, 300)
            }
        }
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_routes
    }
}