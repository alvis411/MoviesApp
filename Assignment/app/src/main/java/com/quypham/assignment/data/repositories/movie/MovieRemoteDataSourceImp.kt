package com.quypham.assignment.data.repositories.movie

import com.quypham.assignment.api.MovieApi
import com.quypham.assignment.api.ApiResult
import com.quypham.assignment.api.response.MovieDetailApiResponse
import com.quypham.assignment.api.response.MovieResponse
import com.quypham.assignment.api.response.ResponseItems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRemoteDataSourceImp @Inject constructor(private val movieApi: MovieApi): MovieRemoteDataSource {

    override suspend fun getTodayTrendingMovie(pageNumber: Int): ApiResult<ResponseItems<MovieResponse>> {
        return movieApi.getTodayTrendingMovies(pageNumber)
    }

    override suspend fun getMovieDetail(movieId: Int): ApiResult<MovieDetailApiResponse> {
        return movieApi.getMovieDetail(movieId)
    }

    override suspend fun searchMovie(query: String,pageNumber: Int): ApiResult<ResponseItems<MovieResponse>> {
        return movieApi.searchMovie(query,pageNumber)
    }
}