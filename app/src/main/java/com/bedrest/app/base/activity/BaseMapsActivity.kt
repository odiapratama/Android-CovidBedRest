package com.bedrest.app.base.activity

import com.bedrest.app.App
import com.bedrest.app.R
import com.bedrest.app.data.model.Availability
import com.bedrest.app.utils.ZoomLevel
import com.bedrest.app.utils.bitmapDescriptorFromVector
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.Gson

interface BaseMapsActivity {
    var map: GoogleMap?
    val markerList: ArrayList<Marker>
    fun mapNotReady()
    fun onMarkerClicked(marker: Marker): Boolean

    fun addMarkers(markers: List<MarkerOptions>, hosiptals: List<Availability>? = null, moveCamera: Boolean = true) {
        if (markerList.isNotEmpty()) markerList.forEach { it.remove() }.also { markerList.clear() }

        markers.forEachIndexed { index, marker ->
            marker
                .icon(bitmapDescriptorFromVector(App.INSTANCE.applicationContext, R.drawable.ic_hospital))
                .alpha(0.7f)

            map?.addMarker(marker)?.let {
                it.tag = Gson().toJson(hosiptals?.get(index))
                markerList.add(it)
            }
        }

        if (moveCamera) {
            moveCameraWithBounds()
        }
    }

    fun addMarkers(marker: MarkerOptions, hospitalCode: String? = null, moveCamera: Boolean = true) {
        if (markerList.isNotEmpty()) markerList.forEach { it.remove() }.also { markerList.clear() }
        marker
            .icon(bitmapDescriptorFromVector(App.INSTANCE.applicationContext, R.drawable.ic_hospital))
            .alpha(0.7f)

        map?.addMarker(marker)?.let {
            markerList.add(it)
        }

        if (moveCamera) {
            moveCameraTo(marker.position, ZoomLevel.STREETS)
        }
    }

    fun moveCameraTo(coord: LatLng, zoomLevel: ZoomLevel) {
        map?.animateCamera(
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

        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngBounds(bounds, 300)
        )
    }
}