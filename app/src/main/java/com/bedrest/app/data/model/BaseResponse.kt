package com.bedrest.app.data.model

data class BaseResponse<T>(
    val status: Int,
    val data: T,
    val error: String
)