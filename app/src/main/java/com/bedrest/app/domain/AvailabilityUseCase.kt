package com.bedrest.app.domain

import com.bedrest.app.domain.model.Availability

interface AvailabilityUseCase {
    suspend fun getHospitalAvailability(province: String): List<Availability>
}