package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel:MainViewModel by activityViewModels{
        MainViewModelFactory(activity?.application as AsteroidApplication)
    }
//    private val viewModel: MainViewModel by lazy {
//        ViewModelProvider(this).get(MainViewModel::class.java)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.apod = viewModel.photo.value

        val asteroidAdapter =MainAsteroidAdapter(AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)

            findNavController().navigate(R.id.detailFragment)
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

//        viewModel.tempList.observe(viewLifecycleOwner){
//            Log.e("MAIN TMP: ", it.size.toString())
//        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /**
         * Make DB queries here
         * */
        when(item.itemId){
            R.id.show_today -> viewModel.getTodayAsteroids()
            R.id.show_week ->viewModel.getAllAsteroids()
            R.id.show_hazardous_today -> viewModel.getPotentiallyHazardousFromToday()
        }
        return true
    }
}
