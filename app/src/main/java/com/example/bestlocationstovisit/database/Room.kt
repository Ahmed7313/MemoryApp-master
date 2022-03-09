package com.example.bestlocationstovisit.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlacedDao {

    @Query("SELECT * FROM DataBaseEntities ORDER BY placeName ASC")
    fun getPlaces() : LiveData<List<DataBaseEntities>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAllPlaces(vararg asteroid: DataBaseEntities)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPlace(place : DataBaseEntities)
}

@Database(entities = [DataBaseEntities::class], version = 1)
abstract class PlacesDatabase : RoomDatabase() {
    abstract val placesDao: PlacedDao
}

private lateinit var INSTANCE: PlacesDatabase

fun getDatabase(context: Context): PlacesDatabase {
    synchronized(PlacesDatabase::class.java){
        if (!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                PlacesDatabase::class.java,
                "Places").build()
        }
    }
    return INSTANCE
}