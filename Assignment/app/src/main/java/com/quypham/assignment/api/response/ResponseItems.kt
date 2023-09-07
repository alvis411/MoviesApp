package com.quypham.assignment.api.response

import com.quypham.assignment.api.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ResponseItems<out T: Response>(@Json(name="page") val page: Int,
                                     @Json(name="results") val results: List<T>,
                                     @Json(name="total_pages") val totalPages: Int,
                                     @Json(name="total_results") val totalResult: Int) : Response