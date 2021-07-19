package com.bedrest.app.data.remote

import com.bedrest.app.data.model.AvailabilityItem
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.data.model.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AvailabilityApi {

    @GET("api/bed")
    suspend fun getHospitalAvailability(
        @Query("prov") province: String,
        @Query("revalidate") revalidate: Boolean = false
    ): BaseResponse<List<AvailabilityItem>>
}