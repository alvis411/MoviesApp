package com.quypham.assignment.data.repositories

import androidx.paging.PagingData
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.MovieDetail
import kotlinx.coroutines.flow.Flow
import com.quypham.assignment.data.error.Result

interface MovieRepository {
    fun getTodayTrendingMovie(): Flow<PagingData<Movie>>
    suspend fun getTrendingMovie(): List<Movie>
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
    suspend fun searchMovie(query: String): Flow<PagingData<Movie>>
}