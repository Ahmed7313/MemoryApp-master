package com.example.bestlocationstovisit.mainview

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.example.bestlocationstovisit.LocalDataSource.PlacesListCallback
import com.example.bestlocationstovisit.LocalDataSource.PlacesRepository
import com.example.bestlocationstovisit.api.domain.Place
import com.example.bestlocationstovisit.database.DataBaseEntities
import com.example.bestlocationstovisit.database.asDomainModel
import com.example.bestlocationstovisit.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesListViewModel (application: Application) : AndroidViewModel(application){

    private val database = getDatabase(application)
    private val repository = PlacesRepository(database)

    private var _placesList = MutableLiveData<List<Place>>()
    val placesList: LiveData<List<Place>>
        get() = _placesList

    private val _navigateToSelectedPlace = MutableLiveData<Place>()
    val navigateToSelectedPlace: LiveData<Place>
        get() = _navigateToSelectedPlace

        val places : LiveData<List<Place>> = Transformations.map(database.placesDao.getPlaces()){
        it.asDomainModel()
    }
    init {
        viewModelScope.launch {
            repository.getPlacesFromFirebase(object:PlacesListCallback {
                override fun onCallback(value: ArrayList<Place>) {
                    _placesList.value = value
                    viewModelScope.launch(Dispatchers.IO) {
                        database.placesDao.insertAllPlaces(*value.asListDomainModel())
                    }
                }
            })
        }
    }

    fun displayPropertyDetails(place: Place) {
        _navigateToSelectedPlace.value = place
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedPlace.value = null
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
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

}