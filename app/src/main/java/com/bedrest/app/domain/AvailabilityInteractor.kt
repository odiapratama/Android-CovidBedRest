package com.bedrest.app.domain

import com.bedrest.app.data.remote.AvailabilityApi
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.utils.NetworkUtil.hitApi
import javax.inject.Inject

class AvailabilityInteractor @Inject constructor(
    private val api: AvailabilityApi
) : AvailabilityUseCase {
    override suspend fun getHospitalAvailability(province: String): List<Availability> {
        return hitApi(api.getHospitalAvailability(province)) {
            it.data.map { it.toAvalability() }
        }
    }
}