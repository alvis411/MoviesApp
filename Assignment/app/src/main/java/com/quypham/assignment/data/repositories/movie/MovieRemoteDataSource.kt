package com.quypham.assignment.data.repositories.movie

import com.quypham.assignment.api.ApiResult
import com.quypham.assignment.api.response.MovieResponse
import com.quypham.assignment.api.response.MovieDetailApiResponse
import com.quypham.assignment.api.response.ResponseItems

interface MovieRemoteDataSource {
    suspend fun getTodayTrendingMovie(pageNumber: Int): ApiResult<ResponseItems<MovieResponse>>
    suspend fun getMovieDetail(movieId: Int): ApiResult<MovieDetailApiResponse>
    suspend fun searchMovie(query: String,pageNumber: Int): ApiResult<ResponseItems<MovieResponse>>
}