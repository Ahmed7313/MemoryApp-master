package com.example.bestlocationstovisit.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bestlocationstovisit.api.domain.Place

@Entity
data class DataBaseEntities constructor(
    @PrimaryKey
    val id: String,
    val placeName: String,
    val placeImage: String,
    val placeLocationLat: String,
    val placeLocationLang : String,
    val placeDescription: String,
    val placeDetails: String)

fun List<DataBaseEntities>.asDomainModel() : List<Place>{
    return map {
        Place(
            id = it.id,
            placeName = it.placeName,
            placeImage = it.placeImage,
            placeLocationLat = it.placeLocationLat,
            placeLocationLang = it.placeLocationLang,
            placeDescription = it.placeDescription,
            placeDetails = it.placeDetails
        )
    }
}


