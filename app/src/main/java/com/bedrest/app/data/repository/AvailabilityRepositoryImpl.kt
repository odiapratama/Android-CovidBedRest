package com.bedrest.app.data.repository

import com.bedrest.app.data.model.Availability
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.data.remote.AvailabilityApi
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
        val result: ResultData<List<Availability>>
        withContext(Dispatchers.IO) {
            result = try {
                val data = availabilityApi.getHospitalAvailability(province, revalidate)
                ResultData.Success(data)
            } catch (e: Exception) {
                ResultData.Error(e)
            }
        }
        return result
    }
}