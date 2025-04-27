package com.example.mapio.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transport_data")
data class TransportData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Date,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val vehicleId: String,
    val routeId: String,
    val occupancy: Int? = null,
    val delay: Int? = null
)

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val stops: List<Stop>
)

data class Stop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val sequence: Int
) 