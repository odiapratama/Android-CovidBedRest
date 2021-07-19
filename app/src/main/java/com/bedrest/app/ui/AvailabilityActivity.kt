package com.bedrest.app.ui

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseMapsActivity
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.databinding.ActivityAvailabilityBinding
import com.bedrest.app.utils.DebounceQuerySearchListener
import com.bedrest.app.utils.IntentUtils.openDialer
import com.bedrest.app.utils.IntentUtils.openMaps
import com.bedrest.app.utils.IntentUtils.openWeb
import com.bedrest.app.utils.StringUtils.checkLatPattern
import com.bedrest.app.utils.StringUtils.checkLonPattern
import com.bedrest.app.utils.StringUtils.convertKeyword
import com.bedrest.app.utils.StringUtils.getStringArray
import com.bedrest.app.utils.StringUtils.toKeywordPattern
import com.bedrest.app.utils.UIUtils.afterMeasured
import com.bedrest.app.utils.UIUtils.gone
import com.bedrest.app.utils.UIUtils.visible
import com.bedrest.app.utils.ZoomLevel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailabilityActivity : BaseMapsActivity<ActivityAvailabilityBinding>() {

    private val availabilityViewModel by viewModels<AvailabilityViewModel>()
    private lateinit var availabilityAdapter: AvailabilityAdapter
    private lateinit var suggestionAdapter: ProvinceSuggestionAdapter
    private lateinit var skeleton: Skeleton
    private val defaultKeyword = "jakarta"
    private var searchKey = defaultKeyword
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun setLayout() = R.layout.activity_availability

    override fun viewOnReady() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        skeleton = binding.clParentCard.createSkeleton()

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
            binding.searchView.setQuery(searchKey.convertKeyword(), false)
        }
        binding.rvSuggestion.adapter = suggestionAdapter

        val layoutParams = binding.motionLayout.layoutParams as CoordinatorLayout.LayoutParams
        bottomSheetBehavior = layoutParams.behavior as BottomSheetBehavior
    }

    override fun initObserver() {
        availabilityViewModel.availability.observe(this, {
            when (it) {
                is ResultData.Loading -> skeleton.showSkeleton()
                is ResultData.Success -> {
                    binding.containerDefaultState.afterMeasured {
                        bottomSheetBehavior.setPeekHeight(
                            binding.containerDefaultState.height,
                            true
                        )
                    }
                    binding.tvCity.text = searchKey.convertKeyword()
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
                    skeleton.showOriginal()
                }
                is ResultData.Error -> {
                    Toast.makeText(
                        this,
                        it.message ?: getString(R.string.error_response),
                        Toast.LENGTH_SHORT
                    ).show()
                    skeleton.showOriginal()
                }
            }
        })
    }

    override fun initListener() {
        binding.root.requestFocus()
        binding.searchView.apply {
            setOnClickListener {
                isIconified = false
            }
        }

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

    override fun onMarkerClicked(marker: Marker): Boolean {
        moveCameraTo(marker.position, ZoomLevel.STREETS)
        marker.showInfoWindow()
        binding.containerMarkerClickedState.afterMeasured {
            bottomSheetBehavior.setPeekHeight(binding.containerMarkerClickedState.height, true)
        }

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
}