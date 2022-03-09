package com.example.bestlocationstovisit.createlocation.selectlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.bestlocationstovisit.R
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.bestlocationstovisit.BuildConfig
import com.example.bestlocationstovisit.createlocation.CreateLocationFragmentArgs
import com.example.bestlocationstovisit.createlocation.CreateLocationViewModel
import com.example.bestlocationstovisit.databinding.FragmentSelectLocationBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.create_location_fragment.*
import java.util.*

class SelectLocationFragment : Fragment() {

    //Use Koin to get the view model of the SaveReminder
    private lateinit var viewModel: CreateLocationViewModel
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latitude : String
    private lateinit var longtitude: String
    private var pickLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        viewModel = ViewModelProvider(this).get(CreateLocationViewModel::class.java)

        //binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        //  add the map setup implementation
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // call this function after the user confirms on the selected location
        binding.saveBtn.setOnClickListener {
            onLocationSelected()
            it.findNavController().navigate(SelectLocationFragmentDirections.
            actionSelectLocationFragmentToCreateLocationFragment(latitude,longtitude))
            Toast.makeText(requireContext(), "Location Added Successfully", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        checkPermissions()
        setMapLongClick(map)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            map.clear()
            // A Snippet is Additional text that's displayed below the title.
            pickLocation = latLng
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    enableMyLocation()
                } else {
                    Toast.makeText(
                        context,
                        "Location permission is required to pick reminder location!",
                        Toast.LENGTH_SHORT
                    ).show()

                    Snackbar.make(
                        binding.fragmentLocation,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }
            }
    }

    private fun onLocationSelected() {
        //         When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        when {
            pickLocation != null -> {
                latitude = pickLocation!!.latitude.toString()
                longtitude = pickLocation!!.latitude.toString()
            }
            else -> {
                Toast.makeText(context, "Please Select a location!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun checkPermissions() {
        if (isPermissionApproved()) {
            enableMyLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun isPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestLocationPermissions() {
        if (isPermissionApproved())
            return
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        map.isMyLocationEnabled = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    //  zoom to the user location after taking his permission
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
                    map.animateCamera(cameraUpdate)
                } else {
                    Toast.makeText(context, "Please Turn on Location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

}

