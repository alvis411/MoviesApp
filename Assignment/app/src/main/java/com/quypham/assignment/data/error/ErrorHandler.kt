package com.quypham.assignment.data.error

interface ErrorHandler {
    fun parseFromException(exception: Throwable): ErrorEntity
    fun parseFromHttpErrorCode(httpErrorCode: Int): ErrorEntity
}