package com.example.bestlocationstovisit.createlocation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestlocationstovisit.LocalDataSource.PlacesRepository
import com.example.bestlocationstovisit.database.getDatabase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class CreateLocationViewModel (application: Application) : AndroidViewModel(application) {


    private val database = getDatabase(application)
    private val repository = PlacesRepository(database)
    val progressBarIndicator = repository.progressBarIndicator

    fun addPlaceToFirebaseAndLocalDatabase(imageData: Uri, placeData : NetwrokDatabase){
        viewModelScope.launch{
            repository.addPlaceToRealTimeDataBase(imageData, placeData)
            //repository.addPlaceToLocalDatabase(placeData)
        }
    }

}