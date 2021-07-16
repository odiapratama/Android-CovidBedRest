package com.bedrest.app.ui

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseActivity
import com.bedrest.app.base.activity.BaseMapsActivity
import com.bedrest.app.data.model.Availability
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.databinding.ActivityAvailabilityBinding
import com.bedrest.app.utils.DebounceQuerySearchListener
import com.bedrest.app.utils.IntentUtils.openDialer
import com.bedrest.app.utils.IntentUtils.openMaps
import com.bedrest.app.utils.IntentUtils.openWeb
import com.bedrest.app.utils.StringUtils.checkLatPattern
import com.bedrest.app.utils.StringUtils.checkLonPattern
import com.bedrest.app.utils.StringUtils.getStringArray
import com.bedrest.app.utils.StringUtils.toCapitalized
import com.bedrest.app.utils.StringUtils.toKeywordPattern
import com.bedrest.app.utils.UIUtils.invisible
import com.bedrest.app.utils.UIUtils.visible
import com.bedrest.app.utils.ZoomLevel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity :
    BaseActivity<ActivityAvailabilityBinding>(), OnMapReadyCallback, BaseMapsActivity {

    private val availabilityViewModel by viewModels<AvailabilityViewModel>()
    private lateinit var availabilityAdapter: AvailabilityAdapter
    private lateinit var suggestionAdapter: ProvinceSuggestionAdapter
    private val defaultKeyword = "jakarta"
    private var searchKey = defaultKeyword
    override var map: GoogleMap? = null
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

        suggestionAdapter = ProvinceSuggestionAdapter(getStringArray(R.array.list_provinces)) {
            searchKey = it.toKeywordPattern()
            availabilityViewModel.getAvailability(searchKey)
        }
        binding.rvSuggestion.adapter = suggestionAdapter

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
                    val total = it.data.sumOf { item -> item.available_bed.toInt() }
                    binding.tvTotal.text = total.toString()
                    if (total == 0) binding.tvTotal.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.tomato_red
                        )
                    )
                    availabilityAdapter.submitList(it.data)
                    binding.tvTitleAvailability.visible()
                    binding.tvTitleBedRest.text = getString(R.string.total_kasur)
                    addMarkersData(it.data)
                }
                is ResultData.Error -> Toast.makeText(
                    this,
                    it.exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addMarkersData(data: List<Availability>) {
        val markers = data.filter {
            it.lat.checkLatPattern() && it.lon.checkLonPattern()
        }.map {
            val coordinate = LatLng(it.lat.toDouble(), it.lon.toDouble())
            MarkerOptions()
                .position(coordinate)
                .title(it.name)
        }

        val hospitalCodes = data.map { it.hospital_code }
        addMarkers(markers, hospitalCodes)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isCompassEnabled = false
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
    }

    override fun onMarkerClicked(marker: Marker) {
        moveCameraTo(marker.position, ZoomLevel.STREETS)
        val hospital = availabilityViewModel.findHospital(marker.tag as String)
        binding.tvCity.text = hospital?.name
        binding.tvTitleAvailability.invisible()
        binding.tvTitleBedRest.text = getString(R.string.tersedia)
        binding.tvTotal.text = hospital?.available_bed
    }

    override fun mapNotReady() {
        Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show()
    }
}