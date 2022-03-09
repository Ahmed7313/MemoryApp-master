package com.example.bestlocationstovisit.api.domain

import android.net.Uri
import android.os.Parcelable
import com.example.bestlocationstovisit.database.DataBaseEntities
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place constructor(val id: String? = null,
                 val placeName: String? = null,
                 val placeImage: String? = null,
                 val placeLocationLat: String? = null,
                 val placeLocationLang: String? = null,
                 val placeDescription: String? = null,
                 val placeDetails: String? = null) : Parcelable

