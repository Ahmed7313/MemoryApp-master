package com.example.bestlocationstovisit.mainview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bestlocationstovisit.api.domain.Place
import com.example.bestlocationstovisit.databinding.CreateLocationListItemBinding

class PlaceAdapter (val onClickListener: OnClickListener): ListAdapter<Place,PlaceAdapter.PlaceViewHolder>(DiffCallback) {

    class PlaceViewHolder(var binding:CreateLocationListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place){
            binding.place=place
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        var inflater= LayoutInflater.from(parent.context)
        var view= CreateLocationListItemBinding.inflate(inflater, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        var place= getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(place)
        }
        holder.bind(place)

    }


    companion object DiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.id == newItem.id
        }

    }


    class OnClickListener(val clickListener: (place :Place) -> Unit){
        fun onClick(place: Place)= clickListener(place)
    }

}