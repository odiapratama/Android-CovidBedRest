package com.bedrest.app.utils

import androidx.lifecycle.MutableLiveData
import com.bedrest.app.data.model.BaseResponse
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.utils.ErrorUtils.getError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

object NetworkUtil {

    fun <RESPONSE, EXPECTED> hitApi(
        api: BaseResponse<RESPONSE>,
        action: (BaseResponse<RESPONSE>) -> EXPECTED
    ): EXPECTED {
        return try {
            if (api.status == 200) action.invoke(api)
            else throw Exception(api.error)

        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    fun <T> CoroutineScope.getResponse(
        api: T,
        data: MutableLiveData<ResultData<T>>,
        doOnSuccess: ((T) -> Unit)? = null,
        doOnError: (() -> Unit)? = null
    ) {
        this.run {
            try {
                data.success(api, doOnSuccess)
            } catch (e: Exception) {
                async { data.fail(e.getError(), doOnError) }
            }
        }
    }

    private fun <T> MutableLiveData<ResultData<T>>.success(data: T, doOnSuccess: ((T) -> Unit)? = null) {
        this.postValue(ResultData.Success(data))
        doOnSuccess?.invoke(data)
    }

    private fun <T> MutableLiveData<ResultData<T>>.fail(error: String, doOnError: (() -> Unit)? = null) {
        this.postValue(ResultData.Error(error))
        doOnError?.invoke()
    }

}