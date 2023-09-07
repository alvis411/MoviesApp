package com.quypham.assignment.data.repositories.movie

import androidx.paging.PagingSource
import com.quypham.assignment.data.db.MovieDetail
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.MovieDatabase
import com.quypham.assignment.data.db.RemoteKeys
import kotlinx.coroutines.flow.Flow


interface MovieLocalDataSource {
    fun getTrendingMoviePagingSource(): PagingSource<Int, Movie>
    suspend fun getAllMovies(): List<Movie>
    suspend fun executeAsTransaction(funcs: suspend MovieDatabase.() -> Unit)
    suspend fun getMovieDetail(movieId: Int): MovieDetail?
    suspend fun insertMovieDetail(movieDetail: MovieDetail)
    suspend fun getRemoteKeyByMovieID(movieId: Int): RemoteKeys?
    suspend fun getRemoteKeyCreationTime(): Long?
}