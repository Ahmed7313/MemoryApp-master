package com.example.bestlocationstovisit.createlocation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.bestlocationstovisit.R
import com.example.bestlocationstovisit.databinding.CreateLocationFragmentBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*


class CreateLocationFragment : Fragment() {

    private val PLACE_TITLE_VALUE = "placeTitle"
    private lateinit var placeName: String
    private lateinit var placeImage : Uri
    private lateinit var placeImageS : String
    private lateinit var placeStatue: String
    private lateinit var placeDetails: String
    private lateinit var placeDescription: String
    private lateinit var placeLocationLat : String
    private lateinit var placeLocationLong : String
    private lateinit var placeRealTimeDatabase : NetwrokDatabase
    private lateinit var binding : CreateLocationFragmentBinding
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMG = 1
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val REQUEST_CODE = 100
    private lateinit var args : CreateLocationFragmentArgs
    private lateinit var placeImageUri : Uri

    companion object {
        fun newInstance() = CreateLocationFragment()
    }

    private lateinit var viewModel: CreateLocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.create_location_fragment, container, false)
        viewModel = ViewModelProvider(this).get(CreateLocationViewModel::class.java)
        args = CreateLocationFragmentArgs.fromBundle(requireArguments())
        binding.savePlaceprogressBar.visibility = View.INVISIBLE

        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getCharSequence(PLACE_TITLE_VALUE)
            binding.placeTitle.setText(savedText)
        }

        binding.selectLocation.setOnClickListener {

            //it.findNavController().navigate(R.id.action_createLocationFragment_to_selectedLocationFragment)
            it.findNavController().navigate(R.id.action_createLocationFragment_to_selectLocationFragment)
        }

        binding.savePlace.setOnClickListener {
            binding.savePlaceprogressBar.visibility = View.VISIBLE
            validateDataAndSendToRealTimeDataBase()
            viewModel.progressBarIndicator.observe(viewLifecycleOwner) {
                if (it) {
                    binding.savePlaceprogressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Sucssess added", Toast.LENGTH_LONG).show()

                }
            }
            it.findNavController().navigate(R.id.action_createLocationFragment_to_placesListFragment)
        }

        binding.addImage.setOnClickListener {
            openGalleryForImage()
        }

        return binding.root
    }

    private fun getUserInput() {
        placeName = binding.placeTitle.text.toString()
        placeDetails = binding.placeDetails.text.toString()
        placeDescription = binding.placeDescription.text.toString()
        placeLocationLat = args.latittude
        placeLocationLong = args.longtittude
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateLocationViewModel::class.java)
    }

    private fun validateDataAndSendToRealTimeDataBase(){
        getUserInput()
        if (checkDataAdded()){
            placeRealTimeDatabase = NetwrokDatabase(
                id = UUID.randomUUID().toString(),
                placeName,
                placeImage.toString(),
                placeLocationLat,
                placeLocationLong,
                placeDescription,
                placeDetails)

            viewModel.addPlaceToFirebaseAndLocalDatabase(placeImageUri, placeRealTimeDatabase)
        }
    }


    private fun checkDataAdded() : Boolean{
        var dataAdded : Boolean = false
        if (binding.addImage != null
            && binding.placeTitle != null
            && binding.placeDescription != null
            && binding.placeDetails != null) {
            dataAdded = true
            return dataAdded
        }else {
            Snackbar.make(
                binding.root,
                "Please fill all data first",
                Snackbar.LENGTH_LONG).show()
            return dataAdded
        }
    }

    private fun getImage(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
    }
    private fun getPlaceImage() {
        val takePicture =  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);

        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            binding.addImage.setImageURI(data?.data) // handle chosen image
            placeImage = Uri.parse(data?.dataString)
            placeImageUri = data?.data!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(
            PLACE_TITLE_VALUE,
            binding.placeTitle.getText()
        ) //<-- Saving operation, change the values to what ever you want.
    }


    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }
}