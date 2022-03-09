package com.example.bestlocationstovisit.detailsFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bestlocationstovisit.databinding.FragmentDetailsBinding
import java.util.*


class DetailsFragment : Fragment() {
    val Fragment.packageManager get() = activity?.packageManager
    lateinit var uri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val place = DetailsFragmentArgs.fromBundle(requireArguments()).selectedPlace
        var lat : Double = place.placeLocationLat!!.toDouble()
        var lang : Double = place.placeLocationLang!!.toDouble()
        val strUri =
            "http://maps.google.com/maps?q=loc:${lat},${lang} (${place.placeName})"
        binding.place = place

        binding.placeLocation.setOnClickListener {
            showMap(Uri.parse(strUri))
        }

        return binding.root
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun showMap(geoLocation: Uri) {
        var intent = Intent(Intent.ACTION_VIEW).apply {
             data = geoLocation
        }
        packageManager?.resolveActivity(intent, 0)

        if (intent.resolveActivity(packageManager!!) != null) {
            startActivity(intent)
        }
    }

}