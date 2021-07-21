package com.bedrest.app.data.repository

import com.bedrest.app.data.model.AvailabilityItem
import com.bedrest.app.data.model.BaseResponse
import com.bedrest.app.data.remote.AvailabilityApi
import javax.inject.Inject

class AvailabilityRepositoryImpl @Inject constructor(
    private val availabilityApi: AvailabilityApi
) : AvailabilityRepository {

    override suspend fun getHospitalAvailability(
        province: String,
        revalidate: Boolean
    ): BaseResponse<List<AvailabilityItem>> {
        return availabilityApi.getHospitalAvailability(province, revalidate)
    }
}