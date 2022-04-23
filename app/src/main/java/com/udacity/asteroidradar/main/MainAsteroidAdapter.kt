package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ListItemBinding

class MainAsteroidAdapter(private val clickListener: AsteroidListener) :
    ListAdapter<Asteroid, MainAsteroidAdapter.MainAsteroidViewHolder>(DiffCallback) {

    class MainAsteroidViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid, clickListener: AsteroidListener) {
            binding.data = asteroid
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAsteroidViewHolder {
        return MainAsteroidViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainAsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)

        holder.bind(asteroid, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

}

class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(amphibian: Asteroid) = clickListener(amphibian)
}