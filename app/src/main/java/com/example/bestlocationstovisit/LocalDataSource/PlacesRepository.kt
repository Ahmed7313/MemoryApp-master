package com.example.bestlocationstovisit.LocalDataSource

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.bestlocationstovisit.api.domain.Place
import com.example.bestlocationstovisit.createlocation.NetwrokDatabase
import com.example.bestlocationstovisit.database.DataBaseEntities
import com.example.bestlocationstovisit.database.PlacesDatabase
import com.example.bestlocationstovisit.database.asDomainModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class PlacesRepository( val placeDatabase: PlacesDatabase) {
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var placesImageFolder: StorageReference
    private val _progressBarIndicator = MutableLiveData<Boolean>()
    val progressBarIndicator: LiveData<Boolean>
        get() = _progressBarIndicator

    init {
        firebaseDatabase = Firebase.database.reference
        placesImageFolder = FirebaseStorage.getInstance().reference.child("placesImages")
    }

    var placesList: ArrayList<Place> = ArrayList()



    suspend fun addPlaceToRealTimeDataBase(imageData: Uri, placeData: NetwrokDatabase) {
        withContext(Dispatchers.IO) {
            firebaseDatabase.child("Best Locations").child(placeData.id).setValue(placeData)
            val imageName: StorageReference =
                placesImageFolder.child("image" + imageData.lastPathSegment)
            imageName.putFile(imageData).addOnSuccessListener {
                imageName.downloadUrl.addOnSuccessListener {
                    firebaseDatabase.child("Best Locations")
                        .child(placeData.id)
                        .child("placeImage").setValue(it.toString())
                    _progressBarIndicator.value = true
                }
            }
        }
    }


    suspend fun getPlacesFromFirebase(myCallback: PlacesListCallback) {
        withContext(Dispatchers.IO){
            firebaseDatabase.child("Best Locations").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (placesSnapShot in dataSnapshot.children) {
                            val place = placesSnapShot.getValue(Place::class.java)
                            placesList.add(place!!)
                        }
                    }
                    myCallback.onCallback(placesList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })
        }
    }
}

