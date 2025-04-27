package com.example.mapio.data

import androidx.room.*
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Dao
interface TransportDao {
    @Query("SELECT * FROM transport_data ORDER BY timestamp DESC")
    suspend fun getAllData(): List<TransportData>

    @Query("SELECT * FROM transport_data WHERE routeId = :routeId ORDER BY timestamp DESC")
    suspend fun getDataByRoute(routeId: String): List<TransportData>

    @Query("SELECT * FROM transport_data WHERE vehicleId = :vehicleId ORDER BY timestamp DESC")
    suspend fun getDataByVehicle(vehicleId: String): List<TransportData>

    @Insert
    suspend fun insertData(data: TransportData)

    @Insert
    suspend fun insertRoute(route: Route)

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRoute(routeId: String): Route?

    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<Route>

    // Bus stop operations
    @Insert
    suspend fun insertBusStop(stop: BusStop)

    @Query("SELECT * FROM bus_stops")
    suspend fun getAllBusStops(): List<BusStop>

    @Query("SELECT * FROM bus_stops WHERE id = :stopId")
    suspend fun getBusStop(stopId: String): BusStop?

    // Bus route operations
    @Insert
    suspend fun insertBusRoute(route: BusRoute)

    @Query("SELECT * FROM bus_routes")
    suspend fun getAllBusRoutes(): List<BusRoute>

    @Query("SELECT * FROM bus_routes WHERE id = :routeId")
    suspend fun getBusRoute(routeId: String): BusRoute?
}

@Database(
    entities = [
        TransportData::class,
        Route::class,
        BusStop::class,
        BusRoute::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TransportDatabase : RoomDatabase() {
    abstract fun transportDao(): TransportDao

    companion object {
        @Volatile
        private var INSTANCE: TransportDatabase? = null

        fun getDatabase(context: android.content.Context): TransportDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransportDatabase::class.java,
                    "transport_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    private val gson = Gson()
    private val stopListType = object : TypeToken<List<Stop>>() {}.type
    private val stringListType = object : TypeToken<List<String>>() {}.type
    private val scheduleType = object : TypeToken<Map<String, List<Int>>>() {}.type

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStopList(value: List<Stop>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStopList(value: String?): List<Stop>? {
        return value?.let { gson.fromJson(it, stopListType) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { gson.fromJson(it, stringListType) }
    }

    @TypeConverter
    fun fromSchedule(value: Map<String, List<Int>>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSchedule(value: String?): Map<String, List<Int>>? {
        return value?.let { gson.fromJson(it, scheduleType) }
    }
} 