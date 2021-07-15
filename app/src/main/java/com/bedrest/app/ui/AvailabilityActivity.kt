package com.bedrest.app.ui

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseActivity
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.databinding.ActivityAvailabilityBinding
import com.bedrest.app.utils.DebounceQuerySearchListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity : BaseActivity<ActivityAvailabilityBinding>() {

    private val availabilityViewModel by viewModels<AvailabilityViewModel>()
    private lateinit var availabilityAdapter: AvailabilityAdapter
    private var searchKey = "jakarta" // default

    override fun setLayout() = R.layout.activity_availability

    override fun viewOnReady() {
        availabilityViewModel.getAvailability("Jakarta")
        availabilityAdapter = AvailabilityAdapter {
            startActivity(Intent(this, MapsActivity::class.java))

        }
        binding.rvAvailability.adapter = availabilityAdapter
        availabilityViewModel.getAvailability(searchKey)

        val layoutParams = binding.motionLayout.layoutParams as CoordinatorLayout.LayoutParams
        val bottomSheetBehavior = layoutParams.behavior as BottomSheetBehavior

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.motionLayout.progress = slideOffset
            }
        })

        binding.searchView.setOnQueryTextListener(
            DebounceQuerySearchListener(lifecycle) {
                searchKey = if (it.isEmpty()) "jakarta" else it
                availabilityViewModel.getAvailability(searchKey)
            })

        availabilityViewModel.availability.observe(this, {
            when (it) {
                is ResultData.Loading -> Unit
                is ResultData.Success -> {
                    binding.tvCity.text = searchKey
                    binding.tvTotal.text =
                        it.data.sumOf { item -> item.available_bed.toInt() }.toString()
                    availabilityAdapter.submitList(it.data)
                }
                is ResultData.Error -> Toast.makeText(
                    this,
                    it.exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}