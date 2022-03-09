
package com.example.bestlocationstovisit.mainview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.bestlocationstovisit.R
import com.example.bestlocationstovisit.api.domain.Place
import com.example.bestlocationstovisit.databinding.PlacesListFragmentBinding

class PlacesListFragment : Fragment() {
    lateinit var places : ArrayList<Place>
    lateinit var  binding : PlacesListFragmentBinding
    private val viewModel: PlacesListViewModel by lazy {
        ViewModelProvider(this).get(PlacesListViewModel::class.java)
    }

    private val placeListAdapter= PlaceAdapter(PlaceAdapter.OnClickListener{
        viewModel.displayPropertyDetails((it))
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlacesListFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        binding.placesRecycler.adapter = placeListAdapter

        binding.createPlace.setOnClickListener {
            it.findNavController().navigate(R.id.action_placesListFragment_to_createLocationFragment)
        }

        binding.viewModel = viewModel

        viewModel.navigateToSelectedPlace.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(PlacesListFragmentDirections.actionPlacesListFragmentToDetailsFragment(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.places.observe(viewLifecycleOwner, Observer { places ->
            places.apply {
                if (places.isEmpty()){
                    binding.animationEmptyList.visibility = View.VISIBLE
                    binding.emptyListText.visibility = View.VISIBLE
                }else{
                    placeListAdapter.submitList(this)
                    binding.animationEmptyList.visibility = View.INVISIBLE
                    binding.emptyListText.visibility = View.INVISIBLE
                }
            }
        })
    }
}