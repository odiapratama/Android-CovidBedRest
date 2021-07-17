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
import com.bedrest.app.utils.UIUtils.gone
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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity :
    BaseActivity<ActivityAvailabilityBinding>(), OnMapReadyCallback, BaseMapsActivity {

    private val availabilityViewModel by viewModels<AvailabilityViewModel>()
    private lateinit var availabilityAdapter: AvailabilityAdapter
    private lateinit var suggestionAdapter: ProvinceSuggestionAdapter
    private val defaultKeyword = "jakarta"
    private var searchKey = defaultKeyword
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
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
        bottomSheetBehavior = layoutParams.behavior as BottomSheetBehavior

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.motionLayout.progress = slideOffset
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
                    bottomSheetBehavior.setPeekHeight(300, true)
                    binding.tvCity.text = searchKey.toCapitalized()
                    val total = it.data.sumOf { item -> item.available_bed.toInt() }
                    binding.tvTotal.text = total.toString()
                    if (total == 0) binding.tvTotal.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.tomato_red
                        )
                    )
                    binding.containerDefaultState.visible()
                    binding.containerMarkerClickedState.gone()
                    availabilityAdapter.submitList(it.data)
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

        addMarkers(markers, data)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isCompassEnabled = false
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
        map?.setOnMarkerClickListener {
            onMarkerClicked(it)
        }
    }

    override fun onMarkerClicked(marker: Marker): Boolean {
        binding.motionLayout
        moveCameraTo(marker.position, ZoomLevel.STREETS)
        marker.showInfoWindow()
        bottomSheetBehavior.setPeekHeight(600, true)

        val hospitalJson = Gson().fromJson((marker.tag as String), Availability::class.java)

        val hospital = availabilityViewModel.findHospital(hospitalJson.hospital_code)
        binding.containerDefaultState.gone()
        binding.containerMarkerClickedState.visible()
        binding.tvHospitalName.text = hospital?.name
        binding.tvTotalHospitalBed.text = hospital?.available_bed
        binding.tvAddress.text = hospital?.address
        binding.tvLastUpdated.text =
            getString(R.string.prefix_last_update, hospital?.updated_at_minutes.toString())

        binding.tvDirection.setOnClickListener {
            openMaps(
                marker.position.latitude.toString(),
                marker.position.longitude.toString(),
                hospitalJson.name
            )
        }

        binding.tvDetail.setOnClickListener {
            openWeb(hospitalJson.bed_detail_link)
        }

        binding.tvPhone.setOnClickListener {
            openDialer(hospitalJson.hotline)
        }

        return true
    }

    override fun mapNotReady() {
        Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show()
    }
}