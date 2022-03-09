package com.example.bestlocationstovisit.LocalDataSource

import com.example.bestlocationstovisit.api.domain.Place

interface PlacesListCallback {
    fun onCallback(value:ArrayList<Place>)
}