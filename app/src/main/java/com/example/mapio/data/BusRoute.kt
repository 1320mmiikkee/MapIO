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
        BusStop("stop1", "Solomou Square", 35.17088137593966, 33.35887808111709),
        BusStop("stop2", "Eleftheria Square", 35.170205758393514, 33.36027135413452),
        BusStop("stop3", "Makarios Avenue", 35.1697, 33.3644),  //doesnt appear
        BusStop("stop4", "University of Cyprus (Central)", 35.14604920235244, 33.408686381115835),
        BusStop("stop5", "General Hospital (Central)", 35.127176120415555, 33.376131202536406),
        BusStop("stop6", "Old Town", 35.17093830589908, 33.364290333732065),
        BusStop("stop7", "Mall of Cyprus", 35.13041213960346, 33.37090626762404),
        BusStop("stop9", "European University Cyprus", 35.16046858359962, 33.33943560758397),
        BusStop("stop10", "The Cyprus Institute", 35.14156177250935, 33.379852284823116),
        BusStop("stop11", "Nicosia Mall", 35.134813353723914, 33.27953707689277),
        BusStop("stop12", "Athalassa National Park", 35.12650661301871, 33.37486669042108),
        BusStop("stop13", "Cyprus Museum", 35.171918113829584, 33.35565182344455),
        BusStop("stop14", "OXI Round About", 35.16974690809104, 33.364543762579665),
        BusStop("stop15", "Ledra Street", 35.173917090009745, 33.36146844064347)
    )

    val ROUTES = listOf(
        BusRoute(
            "route1",
            "Route 1: City Center Loop",
            listOf("stop1", "stop15", "stop2", "stop13", "stop3", "stop6"),
            mapOf(
                "stop1" to listOf(480, 540, 600, 660, 720), // 8:00, 9:00, 10:00, 11:00, 12:00
                "stop15" to listOf(485, 545, 605, 665, 725),
                "stop2" to listOf(490, 550, 610, 670, 730),
                "stop13" to listOf(495, 555, 615, 675, 735),
                "stop3" to listOf(500, 560, 620, 680, 740),
                "stop6" to listOf(505, 565, 625, 685, 745)
            )
        ),
        BusRoute(
            "route2",
            "Route 2: University Express",
            listOf("stop2", "stop9", "stop4", "stop10", "stop12"),
            mapOf(
                "stop2" to listOf(510, 570, 630, 690, 750),
                "stop9" to listOf(515, 575, 635, 695, 755),
                "stop4" to listOf(520, 580, 640, 700, 760),
                "stop10" to listOf(525, 585, 645, 705, 765),
                "stop12" to listOf(530, 590, 650, 710, 770)
            )
        ),
        BusRoute(
            "route3",
            "Route 3: Shopping Line",
            listOf("stop3", "stop7", "stop11", "stop14", "stop1"),
            mapOf(
                "stop3" to listOf(525, 585, 645, 705, 765),
                "stop7" to listOf(535, 595, 655, 715, 775),
                "stop11" to listOf(545, 605, 665, 725, 785),
                "stop14" to listOf(555, 615, 675, 735, 795),
                "stop1" to listOf(565, 625, 685, 745, 805)
            )
        ),
        BusRoute(
            "route4",
            "Route 4: Hospital Express",
            listOf("stop1", "stop5", "stop14", "stop3"),
            mapOf(
                "stop1" to listOf(480, 600, 720, 840),  // 8:00, 10:00, 12:00, 14:00
                "stop5" to listOf(495, 615, 735, 855),
                "stop14" to listOf(510, 630, 750, 870),
                "stop3" to listOf(525, 645, 765, 885)
            )
        )
    )
} 