package com.quypham.assignment.data.repositories.movie

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.withTransaction
import com.quypham.assignment.data.db.MovieDatabase
import com.quypham.assignment.data.db.MovieDetail
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.RemoteKeys
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieLocalDataSourceImp @Inject constructor(private val movieDatabase: MovieDatabase): MovieLocalDataSource {
    override fun getTrendingMoviePagingSource(): PagingSource<Int, Movie> {
        return movieDatabase.getMovieDao().getMoviePagingSource()
    }

    override suspend fun getAllMovies(): List<Movie> {
        return movieDatabase.getMovieDao().getAllMovie()
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail? {
        return movieDatabase.getMovieDetailDao().getMovieDetailDao(movieId)
    }

    override suspend fun executeAsTransaction(funcs: suspend MovieDatabase.() -> Unit) {
        movieDatabase.withTransaction {
            funcs(movieDatabase)
        }
    }

    override suspend fun insertMovieDetail(movieDetail: MovieDetail) {
        movieDatabase.getMovieDetailDao().insertMovieDetail(movieDetail)
    }

    override suspend fun getRemoteKeyByMovieID(movieId: Int): RemoteKeys? {
        return movieDatabase.getRemoteKeysDao().getRemoteKeyByMovieID(movieId)
    }

    override suspend fun getRemoteKeyCreationTime(): Long? {
        return movieDatabase.getRemoteKeysDao().getCreationTime()
    }
}