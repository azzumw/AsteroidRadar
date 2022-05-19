package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.getTodaysDate
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.work.RefreshDataWorker

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(activity?.application as AsteroidApplication, AsteroidRepository(
            AppDatabase.getDatabase(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        checkWorkRequestStatus()

        val asteroidAdapter = MainAsteroidAdapter(AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)

            findNavController().navigate(R.id.detailFragment)
        })

        viewModel.filteredAsteroids.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()){
                //show textview
                binding.noDataMessage.isVisible = true
                binding.asteroidRecycler.isVisible = false
            }else{
                binding.noDataMessage.isVisible = false
                binding.asteroidRecycler.isVisible = true
            }
        })


        binding.asteroidRecycler.adapter = asteroidAdapter

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
        when (item.itemId) {
            R.id.show_today -> viewModel.selectFilter(1)
            R.id.show_week -> viewModel.selectFilter(2)
            R.id.show_hazardous_today -> viewModel.selectFilter(3)
            else -> viewModel.selectFilter(2)
        }
        return true
    }

    private fun checkWorkRequestStatus() {
        val workRequest = (requireActivity().application as AsteroidApplication).oneTimeWorkRequest
        WorkManager.getInstance(requireContext().applicationContext)
            .getWorkInfoByIdLiveData(workRequest.id).observe(viewLifecycleOwner, Observer {
                if (it.state.isFinished) {
                    Log.e("CheckWorkReqState- ",it.state.toString())
                }
            })
    }
}
