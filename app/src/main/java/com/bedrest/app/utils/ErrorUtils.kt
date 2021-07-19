package com.bedrest.app.utils

import com.bedrest.app.App
import com.bedrest.app.R
import com.bedrest.app.data.model.BaseResponse
import com.bedrest.app.utils.StringUtils.stringSuspending
import com.google.gson.Gson
import retrofit2.HttpException

object ErrorUtils {

    suspend fun Exception.getError(): String {
        return when (this) {
            is HttpException -> {
                val errorResponse: BaseResponse<*> = Gson().fromJson(
                    response()?.errorBody()?.stringSuspending(),
                    BaseResponse::class.java
                )
                errorResponse.error
            }
            else -> App.INSTANCE.applicationContext.getString(R.string.error_response)
        }
    }
}