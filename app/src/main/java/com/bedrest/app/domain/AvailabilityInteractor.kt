package com.bedrest.app.domain

import com.bedrest.app.data.repository.AvailabilityRepository
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.utils.NetworkUtil.hitApi
import javax.inject.Inject

class AvailabilityInteractor @Inject constructor(
    private val availabilityRepository: AvailabilityRepository
) : AvailabilityUseCase {

    override suspend fun getHospitalAvailability(province: String): List<Availability> {
        return hitApi(availabilityRepository.getHospitalAvailability(province)) { response ->
            response.data.map { it.toAvailability() }
        }
    }
}