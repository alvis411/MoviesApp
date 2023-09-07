package com.quypham.assignment.data.repositories.movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.quypham.assignment.data.error.Result
import com.quypham.assignment.api.ApiResult
import com.quypham.assignment.data.common.mapToEntity
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.MovieDetail
import com.quypham.assignment.data.error.NetworkErrorHandler
import com.quypham.assignment.data.paging.MovieSearchPagingSource
import com.quypham.assignment.data.paging.MovieTrendingPagingSource
import com.quypham.assignment.data.repositories.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_PAGING_SIZE = 5
private const val INITIAL_LOAD_SIZE = 10

@Singleton
class MovieRepositoryImp @Inject constructor(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource,): MovieRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getTodayTrendingMovie(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = MAX_PAGING_SIZE,
                prefetchDistance = 2,
                initialLoadSize = INITIAL_LOAD_SIZE, // How many items you want to load initially
            ),
            pagingSourceFactory = {
                // The pagingSourceFactory lambda should always return a brand new PagingSource
                // when invoked as PagingSource instances are not reusable.
                movieLocalDataSource.getTrendingMoviePagingSource()
            },
            remoteMediator = MovieTrendingPagingSource(
                movieRemoteDataSource,
                movieLocalDataSource,
            )
        ).flow
    }

    override suspend fun getTrendingMovie(): List<Movie> = withContext(Dispatchers.IO) {
        return@withContext movieLocalDataSource.getAllMovies()
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        val localMovieDetail = movieLocalDataSource.getMovieDetail(movieId)
        return if (localMovieDetail != null) {
            Result.Success(localMovieDetail)
        } else {
            when (val apiResult = movieRemoteDataSource.getMovieDetail(movieId)) {
                is ApiResult.ApiSuccess -> {
                    val movieDetail = apiResult.data.mapToEntity()
                    movieLocalDataSource.insertMovieDetail(movieDetail)
                    Result.Success(movieDetail)
                }
                is ApiResult.ApiError -> {
                    Result.Error(NetworkErrorHandler.parseFromHttpErrorCode(apiResult.code))
                }
                is ApiResult.ApiException -> {
                    Result.Error(NetworkErrorHandler.parseFromException(apiResult.e))
                }

            }
        }
    }

    override suspend fun searchMovie(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGING_SIZE, prefetchDistance = 2),
            pagingSourceFactory = {
                MovieSearchPagingSource(query, movieRemoteDataSource)
            }
        ).flow
    }
}