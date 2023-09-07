package com.quypham.assignment.ui.utils

import com.quypham.assignment.R
import com.quypham.assignment.data.error.ErrorEntity

object UiMessageParser {
    fun getErrorReasonFromErrorEntity(errorEntity: ErrorEntity): Int {
        return when (errorEntity) {
            is ErrorEntity.NoInternetConnectionError -> {
                R.string.error_no_internet
            }
            is ErrorEntity.NetworkTimeoutError -> {
                R.string.error_network_timeout
            }
            is ErrorEntity.UnknownError -> {
                R.string.error_unknown
            }
        }
    }
}