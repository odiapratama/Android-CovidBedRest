package com.bedrest.app.data.repository

import com.bedrest.app.data.model.Availability
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.data.remote.AvailabilityApi
import com.bedrest.app.utils.ErrorUtils.getError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AvailabilityRepositoryImpl @Inject constructor(
    private val availabilityApi: AvailabilityApi
) : AvailabilityRepository {

    override suspend fun getHospitalAvailability(
        province: String,
        revalidate: Boolean
    ): ResultData<List<Availability>> {
        var result: ResultData<List<Availability>>
        withContext(Dispatchers.IO) {
            result = try {
                val response = availabilityApi.getHospitalAvailability(province, revalidate)
                if (response.status == 200) ResultData.Success(response.data ?: emptyList())
                else ResultData.Error(response.error)
            } catch (e: Exception) {
                ResultData.Error(e.getError())
            }
        }
        return result
    }
}