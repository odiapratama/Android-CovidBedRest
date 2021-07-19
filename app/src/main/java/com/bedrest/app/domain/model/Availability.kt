package com.bedrest.app.domain.model

data class Availability(
    val name: String,
    val address: String,
    val available_bed: String,
    val bed_queue: Int,
    val hotline: String,
    val bed_detail_link: String,
    val hospital_code: String,
    val updated_at_minutes: Int,
    val lat: String,
    val lon: String
)