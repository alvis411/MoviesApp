package com.quypham.assignment.data.error

sealed class ErrorEntity : Throwable() {
    object UnknownError: ErrorEntity()
    object NoInternetConnectionError: ErrorEntity()
    object NetworkTimeoutError: ErrorEntity()
}