package com.quypham.assignment.api

import com.quypham.assignment.api.response.MovieDetailApiResponse
import com.quypham.assignment.api.response.MovieResponse
import com.quypham.assignment.api.response.ResponseItems
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("trending/movie/day")
    suspend fun getTodayTrendingMovies(@Query("page") pageNumber: Int): ApiResult<ResponseItems<MovieResponse>>

    @GET("movie/{id}")
    suspend fun getMovieDetail(@Path("id") movieId: Int): ApiResult<MovieDetailApiResponse>

    @GET("search/movie")
    suspend fun searchMovie(@Query("query") query: String,@Query("page") pageNumber: Int): ApiResult<ResponseItems<MovieResponse>>
}