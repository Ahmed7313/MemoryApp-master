package com.example.bestlocationstovisit.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.bestlocationstovisit.R
import com.example.bestlocationstovisit.api.domain.Place
import com.squareup.picasso.Picasso


@BindingAdapter("placeImage")
fun bindPlaceImage( imageView : ImageView, imageUri: String ){
    Picasso.get().load(imageUri).placeholder(R.drawable.placeholder_place)
        .into(imageView);
}

@BindingAdapter("placeTitle")
fun bindPlaceTitle(textView: TextView, place: Place){
    textView.text = place.placeName
}


@BindingAdapter("placeDescription")
fun bindPlaceDescription(textView: TextView, place: Place){
    textView.text = place.placeDescription
}

@BindingAdapter("placeLocation")
fun bindPlaceLocationANdOpenMap(textView: TextView, place: Place){

}
