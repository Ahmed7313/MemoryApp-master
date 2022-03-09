package com.example.bestlocationstovisit.work

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.example.bestlocationstovisit.LocalDataSource.PlacesListCallback
import com.example.bestlocationstovisit.LocalDataSource.PlacesRepository
import com.example.bestlocationstovisit.api.domain.Place
import com.example.bestlocationstovisit.database.DataBaseEntities
import com.example.bestlocationstovisit.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object{
        const val WORK_NAME = "PlacesWorker"
    }
    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = PlacesRepository(database)

        return try {
            repository.getPlacesFromFirebase(object: PlacesListCallback {
                override fun onCallback(value: ArrayList<Place>) {
                    database.placesDao.insertAllPlaces(*value.asListDomainModel())
                }
            })
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}

fun ArrayList<Place>.asListDomainModel(): Array<DataBaseEntities> {
    return map {
        DataBaseEntities(
            id = it.id.toString(),
            placeName = it.placeName.toString(),
            placeImage = it.placeImage.toString(),
            placeLocationLat = it.placeLocationLat.toString(),
            placeLocationLang = it.placeLocationLang.toString(),
            placeDescription = it.placeDescription.toString(),
            placeDetails = it.placeDetails.toString()
        )
    }
        .toTypedArray()
}