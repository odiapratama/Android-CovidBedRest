package com.bedrest.app.base.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bedrest.app.App
import com.bedrest.app.R
import com.bedrest.app.data.model.Availability
import com.bedrest.app.utils.ImageUtils.bitmapDescriptorFromVector
import com.bedrest.app.utils.ZoomLevel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.gson.Gson

abstract class BaseMapsActivity<V : ViewDataBinding> :
    AppCompatActivity(), BaseViewBindingActivity<V> by BaseViewBindingActivityImpl(),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val markerList: ArrayList<Marker> = ArrayList()

    @LayoutRes
    abstract fun setLayout(): Int

    abstract fun viewOnReady()

    open fun initObserver() = Unit

    open fun initListener() = Unit

    abstract fun onMarkerClicked(marker: Marker): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding(DataBindingUtil.setContentView(this, setLayout()), this)
        binding.lifecycleOwner = this
        viewOnReady()
        initListener()
        initObserver()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isCompassEnabled = false
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.maps_dark_style
                )
            )
            Configuration.UI_MODE_NIGHT_NO -> map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.maps_light_style
                )
            )
            Configuration.UI_MODE_NIGHT_UNDEFINED -> map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.maps_light_style
                )
            )
        }
        map.setOnMarkerClickListener {
            onMarkerClicked(it)
        }
    }

    fun addMarkers(
        markers: List<MarkerOptions>,
        hosiptals: List<Availability>? = null,
        moveCamera: Boolean = true
    ) {
        if (markerList.isNotEmpty()) markerList.forEach { it.remove() }.also { markerList.clear() }

        markers.forEachIndexed { index, marker ->
            marker
                .icon(
                    bitmapDescriptorFromVector(
                        App.INSTANCE.applicationContext,
                        R.drawable.ic_hospital
                    )
                )
                .alpha(0.7f)

            map.addMarker(marker)?.let {
                it.tag = Gson().toJson(hosiptals?.get(index))
                markerList.add(it)
            }
        }

        if (moveCamera) {
            moveCameraWithBounds()
        }
    }

    fun addMarkers(marker: MarkerOptions, moveCamera: Boolean = true) {
        if (markerList.isNotEmpty()) markerList.forEach { it.remove() }.also { markerList.clear() }
        marker
            .icon(
                bitmapDescriptorFromVector(
                    App.INSTANCE.applicationContext,
                    R.drawable.ic_hospital
                )
            )
            .alpha(0.7f)

        map.addMarker(marker)?.let {
            markerList.add(it)
        }

        if (moveCamera) {
            moveCameraTo(marker.position, ZoomLevel.STREETS)
        }
    }

    fun moveCameraTo(coord: LatLng, zoomLevel: ZoomLevel) {
        map.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(coord, zoomLevel.value)
        )
    }

    private fun moveCameraWithBounds() {
        val latitudes = markerList.map { it.position.latitude }.sortedDescending()
        val longitudes = markerList.map { it.position.longitude }.sortedDescending()

        val southWestCoord = LatLng(latitudes.last(), longitudes.last())
        val northEastCoord = LatLng(latitudes.first(), longitudes.first())

        val bounds = LatLngBounds(southWestCoord, northEastCoord)

        map.animateCamera(
            CameraUpdateFactory
                .newLatLngBounds(bounds, 300)
        )
    }
}