package com.bedrest.app.ui

import android.content.Intent
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseActivity
import com.bedrest.app.databinding.ActivityAvailabilityBinding
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity : BaseActivity<ActivityAvailabilityBinding>() {

    /*lateinit var availabilityViewModel: AvailabilityViewModel by viewModels()*/

    override fun setLayout() = R.layout.activity_availability

    override fun viewOnReady() {
        binding.tvHellp.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
        /*availabilityViewModel.getAvailability("Jakarta")

        availabilityViewModel.availability.observe(this, {
            when (it) {
                is ResultData.Loading -> {
                }
                is ResultData.Success -> Toast.makeText(
                    this,
                    it.data.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                is ResultData.Error -> Toast.makeText(
                    this,
                    it.exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })*/
    }
}