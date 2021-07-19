package com.bedrest.app.data.repository

import com.bedrest.app.data.model.AvailabilityItem
import com.bedrest.app.data.model.BaseResponse
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.data.model.ResultData

interface AvailabilityRepository {

    suspend fun getHospitalAvailability(
        province: String,
        revalidate: Boolean = false
    ) : BaseResponse<List<AvailabilityItem>>
}