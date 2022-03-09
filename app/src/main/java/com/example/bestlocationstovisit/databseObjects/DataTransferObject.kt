package com.example.bestlocationstovisit.createlocation

import androidx.room.PrimaryKey
import com.example.bestlocationstovisit.database.DataBaseEntities


data class NetwrokDatabase constructor(
    val id : String,
    val placeName: String,
    val placeImage: String,
    val placeLocationLat: String,
    val placeLocationLang: String,
    val placeDescription: String,
    val placeDetails: String)

fun NetwrokDatabase.asDataBaseEntities() = DataBaseEntities(
    id = id,
    placeName = placeName,
    placeImage = placeImage,
    placeLocationLat = placeLocationLat,
    placeLocationLang = placeLocationLang,
    placeDescription = placeDescription,
    placeDetails = placeDetails
)


