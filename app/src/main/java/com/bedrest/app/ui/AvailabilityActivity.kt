package com.bedrest.app.ui

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseActivity
import com.bedrest.app.base.activity.BaseMapsActivity
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.databinding.ActivityAvailabilityBinding
import com.bedrest.app.utils.ZoomLevel
import com.bedrest.app.utils.DebounceQuerySearchListener
import com.bedrest.app.utils.IntentUtils.openDialer
import com.bedrest.app.utils.IntentUtils.openMaps
import com.bedrest.app.utils.IntentUtils.openWeb
import com.bedrest.app.utils.StringUtils.toCapitalized
import com.bedrest.app.utils.StringUtils.toKeywordPattern
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity :
    BaseActivity<ActivityAvailabilityBinding>(), OnMapReadyCallback, BaseMapsActivity {

    private val availabilityViewModel by viewModels<AvailabilityViewModel>()
    private lateinit var availabilityAdapter: AvailabilityAdapter
    private val defaultKeyword = "jakarta"
    private var searchKey = defaultKeyword
    override var mMap: GoogleMap? = null
    override val markerList = ArrayList<Marker>(emptyList())

    override fun setLayout() = R.layout.activity_availability

    override fun viewOnReady() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        availabilityViewModel.getAvailability(searchKey)
        availabilityAdapter = AvailabilityAdapter({ lat, long, key ->
            openMaps(lat, long, key)
        }, {
            openWeb(it)
        }, {
            openDialer(it)
        })
        binding.rvAvailability.adapter = availabilityAdapter

        val layoutParams = binding.motionLayout.layoutParams as CoordinatorLayout.LayoutParams
        val bottomSheetBehavior = layoutParams.behavior as BottomSheetBehavior

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.motionLayout.progress = slideOffset
                if (slideOffset == 0f) binding.fabCurrentLocation.show()
                else binding.fabCurrentLocation.hide()
            }
        })

        binding.searchView.setOnQueryTextListener(
            DebounceQuerySearchListener(lifecycle) {
                searchKey = if (it.isEmpty()) defaultKeyword else it.toKeywordPattern()
                availabilityViewModel.getAvailability(searchKey)
            })

        availabilityViewModel.availability.observe(this, {
            when (it) {
                is ResultData.Loading -> Unit
                is ResultData.Success -> {
                    binding.tvCity.text = searchKey.toCapitalized()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val jktCoord = LatLng(-6.200000, 106.816666)
        val jktMarker = MarkerOptions().position(jktCoord)
        addMarkers(jktMarker, false)
        moveCameraTo(jktCoord, ZoomLevel.CONTINENT)

        mMap?.setOnMarkerClickListener {
            onMarkerClicked(it)
            true
        }
    }

    override fun onMarkerClicked(marker: Marker) {
        moveCameraTo(marker.position, ZoomLevel.STREETS)
    }

    override fun mapNotReady() {
        Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show()
    }
}