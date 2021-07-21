package com.bedrest.app.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bedrest.app.data.model.BaseResponse
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.utils.ErrorUtils.getError
import kotlinx.coroutines.launch

object NetworkUtil {

    fun <RESPONSE, EXPECTED> hitApi(
        api: BaseResponse<RESPONSE>,
        action: (BaseResponse<RESPONSE>) -> EXPECTED,
    ): EXPECTED {
        return try {
            if (api.status == 200) action.invoke(api)
            else throw Exception(api.error)

        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    fun <T> ViewModel.safeApiCall(
        data: MutableLiveData<ResultData<T>>,
        block: suspend MutableLiveData<ResultData<T>>.() -> Unit,
        doOnError: ((Exception) -> Unit)? = null
    ) {
        data.postValue(ResultData.Loading)
        viewModelScope.launch {
            try {
                block(data)
            } catch (e: Exception) {
                launch { data.fail(e.getError(), e, doOnError) }
            }
        }
    }

    fun <T> MutableLiveData<ResultData<T>>.getResponse(
        api: T,
        doOnSuccess: ((T) -> Unit)? = null
    ) {
        success(api, doOnSuccess)
    }

    private fun <T> MutableLiveData<ResultData<T>>.success(
        data: T,
        doOnSuccess: ((T) -> Unit)? = null
    ) {
        postValue(ResultData.Success(data))
        doOnSuccess?.invoke(data)
    }

    private fun <T> MutableLiveData<ResultData<T>>.fail(
        error: String,
        e: Exception,
        doOnError: ((Exception) -> Unit)? = null
    ) {
        postValue(ResultData.Error(error))
        doOnError?.invoke(e)
    }

}