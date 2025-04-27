package com.example.mapio.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "bus_stops")
data class BusStop(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable

@Entity(tableName = "bus_routes")
data class BusRoute(
    @PrimaryKey val id: String,
    val name: String,
    val stops: List<String>, // List of stop IDs
    val schedule: Map<String, List<Int>> // Map of stop ID to list of arrival times in minutes from midnight
)

// Sample data for Nicosia bus routes
object NicosiaBusRoutes {
    val STOPS = listOf(
        BusStop("stop1", "Solomou Square", 35.1706636, 33.358684),
        BusStop("stop2", "Eleftheria Square", 35.1702039, 33.3601883),
        BusStop("stop3", "Makarios Avenue", 35.166474, 33.361785),
        BusStop("stop4", "University of Cyprus (Central)", 35.1705, 33.3610),
        BusStop("stop5", "General Hospital (Central)", 35.1708, 33.3615),
        BusStop("stop6", "Old Town", 35.16957, 33.36200)
    )

    val ROUTES = listOf(
        BusRoute(
            "route1",
            "Route 1: City Center",
            listOf("stop1", "stop2", "stop3"),
            mapOf(
                "stop1" to listOf(480, 540, 600, 660, 720), // 8:00, 9:00, 10:00, 11:00, 12:00
                "stop2" to listOf(490, 550, 610, 670, 730),
                "stop3" to listOf(500, 560, 620, 680, 740)
            )
        ),
        BusRoute(
            "route2",
            "Route 2: University Line",
            listOf("stop2", "stop4", "stop5"),
            mapOf(
                "stop2" to listOf(510, 570, 630, 690, 750),
                "stop4" to listOf(520, 580, 640, 700, 760),
                "stop5" to listOf(530, 590, 650, 710, 770)
            )
        ),
        BusRoute(
            "route3",
            "Route 3: Old Town",
            listOf("stop3", "stop6", "stop1"),
            mapOf(
                "stop3" to listOf(525, 585, 645, 705, 765),
                "stop6" to listOf(535, 595, 655, 715, 775),
                "stop1" to listOf(545, 605, 665, 725, 785)
            )
        )
    )
} 