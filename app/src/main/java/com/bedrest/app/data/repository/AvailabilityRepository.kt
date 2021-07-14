package com.bedrest.app.data.repository

import com.bedrest.app.data.model.Availability
import com.bedrest.app.data.model.ResultData

interface AvailabilityRepository {

    suspend fun getHospitalAvailability(
        province: String,
        revalidate: Boolean = false
    ) : ResultData<List<Availability>>
}