package com.bedrest.app.data.model

import com.bedrest.app.domain.model.Availability
import com.google.gson.annotations.SerializedName

data class AvailabilityItem(
    @SerializedName("name")
    val name: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("available_bed")
    val available_bed: String?,

    @SerializedName("bed_queue")
    val bed_queue: Int?,

    @SerializedName("hotline")
    val hotline: String?,

    @SerializedName("bed_detail_link")
    val bed_detail_link: String?,

    @SerializedName("hospital_code")
    val hospital_code: String?,

    @SerializedName("updated_at_minutes")
    val updated_at_minutes: Int?,

    @SerializedName("lat")
    val lat: String?,

    @SerializedName("lon")
    val lon: String?
) {
    fun toAvalability(): Availability {
        return Availability(
            name = name.orEmpty(),
            address = address.orEmpty(),
            available_bed = available_bed.orEmpty(),
            bed_queue = bed_queue ?: 0,
            hotline = hotline.orEmpty(),
            bed_detail_link = bed_detail_link.orEmpty(),
            hospital_code = hospital_code.orEmpty(),
            updated_at_minutes = updated_at_minutes ?: 0,
            lat = lat.orEmpty(),
            lon = lon.orEmpty()
        )
    }
}