package com.quypham.assignment.data.error

import com.quypham.assignment.api.common.NoInternetConnectionThrowable


const val HTTP_ERROR_BAD_REQUEST = 400
const val HTTP_ERROR_UNAUTHORIZED = 401
const val HTTP_ERROR_REQUEST_TIMEOUT= 408

object NetworkErrorHandler : ErrorHandler {
    override fun parseFromException(exception: Throwable): ErrorEntity {
        return when (exception) {
            is NoInternetConnectionThrowable -> {
                ErrorEntity.NoInternetConnectionError
            }
            else -> {
                ErrorEntity.UnknownError
            }
        }
    }

    override fun parseFromHttpErrorCode(httpErrorCode: Int): ErrorEntity {
        return when (httpErrorCode) {
            HTTP_ERROR_REQUEST_TIMEOUT -> {
                ErrorEntity.NetworkTimeoutError
            }
            else -> {
                ErrorEntity.UnknownError
            }
        }
    }
}