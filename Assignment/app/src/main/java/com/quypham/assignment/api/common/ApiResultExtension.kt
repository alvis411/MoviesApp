package com.quypham.assignment.api.common

import com.quypham.assignment.api.ApiResult
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> handleApi(
    execute: () -> Response<T>
): ApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResult.ApiSuccess(body)
        } else {
            ApiResult.ApiError(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        ApiResult.ApiError(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        ApiResult.ApiException(e)
    }
}

suspend fun <T : Any> ApiResult<T>.onSuccess(
    executable: suspend (T) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiSuccess<T>) {
        executable(data)
    }
}

suspend fun <T : Any> ApiResult<T>.onError(
    executable: suspend (code: Int, message: String?) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiError<T>) {
        executable(code, message)
    }
}

suspend fun <T : Any> ApiResult<T>.onException(
    executable: suspend (e: Throwable) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiException<T>) {
        executable(e)
    }
}