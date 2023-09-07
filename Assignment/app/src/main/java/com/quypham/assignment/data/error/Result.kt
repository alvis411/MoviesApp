package com.quypham.assignment.data.error


/**
 * Result return from repository to lower layer
 */
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val error: ErrorEntity) : Result<T>()
}