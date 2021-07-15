package com.bedrest.app.ui

import android.widget.Toast
import com.bedrest.app.R
import com.bedrest.app.base.activity.BaseActivity
import com.bedrest.app.base.activity.BaseMapsActivity
import com.bedrest.app.databinding.ActivityMapsBinding
import com.bedrest.app.util.ZoomLevel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback, BaseMapsActivity {
    override var mMap: GoogleMap? = null
    override val markerList = ArrayList<Marker>(emptyList())
    override fun setLayout(): Int = R.layout.activity_maps

    override fun viewOnReady() {
        initMap()
        initAction()
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initAction() {
        binding.btnChangeLocation.setOnClickListener {
            addMultipleMarkers()
        }
    }

    private fun addMultipleMarkers() {
        val markerList = listOf(
            MarkerOptions()
                .position(LatLng(-2.1717096296249907, 113.8720229169403))
                .title("Marker in Testing 1"),
            MarkerOptions()
                .position(LatLng(-1.7243777804506009, 114.83201680852889))
                .title("Marker in Testing 2")
        )

        addMarkers(markerList)
    }

    override fun onMarkerClicked(marker: Marker) {
        moveCameraTo(marker.position, ZoomLevel.STREETS)
    }

    override fun mapNotReady() {
        Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show()
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
}