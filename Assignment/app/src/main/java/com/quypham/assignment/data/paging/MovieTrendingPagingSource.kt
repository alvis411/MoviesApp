package com.quypham.assignment.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.quypham.assignment.api.common.onError
import com.quypham.assignment.api.common.onException
import com.quypham.assignment.api.common.onSuccess
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.RemoteKeys
import com.quypham.assignment.data.common.mapToEntity
import com.quypham.assignment.data.error.NetworkErrorHandler
import com.quypham.assignment.data.repositories.movie.MovieLocalDataSource
import com.quypham.assignment.data.repositories.movie.MovieRemoteDataSource
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class MovieTrendingPagingSource constructor(private val movieApi: MovieRemoteDataSource,
                                            private val movieLocalDataSource: MovieLocalDataSource) : RemoteMediator<Int, Movie>() {

    override suspend fun initialize(): InitializeAction {
        //TODO convey cache time
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return if (System.currentTimeMillis() - (movieLocalDataSource.getRemoteKeyCreationTime()
                ?: 0) < cacheTimeout
        ) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Movie>
    ): MediatorResult {
        val pageNumber = when (loadType) {
            LoadType.REFRESH -> {
                //New Query so clear the DB
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        val apiResult = movieApi.getTodayTrendingMovie(pageNumber)
        var mediatorResult: MediatorResult = MediatorResult.Success(true)

        apiResult.onSuccess { response ->
            val movies = response.mapToEntity()
            val endOfPagination = movies.isEmpty()

            movieLocalDataSource.executeAsTransaction {
                if (loadType == LoadType.REFRESH) {
                    getRemoteKeysDao().clearRemoteKeys()
                    getMovieDao().clearAllMovies()
                }
                val prevKey = if (pageNumber > 1) pageNumber - 1 else null
                val nextKey = if (endOfPagination) null else pageNumber + 1
                val remoteKeys = movies.map {
                    RemoteKeys(
                        movieID = it.id,
                        prevKey = prevKey,
                        currentPage = pageNumber,
                        nextKey = nextKey
                    )
                }

                getRemoteKeysDao().insertAll(remoteKeys)
                getMovieDao().insertAll(movies)
            }
            mediatorResult = MediatorResult.Success(endOfPaginationReached = endOfPagination)
        }.onError { code, _ ->
            mediatorResult = MediatorResult.Error(NetworkErrorHandler.parseFromHttpErrorCode(code))
        }.onException {
            mediatorResult = MediatorResult.Error(NetworkErrorHandler.parseFromException(it))
        }

        return mediatorResult
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Movie>): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                movieLocalDataSource.getRemoteKeyByMovieID(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { movie ->
            movieLocalDataSource.getRemoteKeyByMovieID(movie.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { movie ->
            movieLocalDataSource.getRemoteKeyByMovieID(movie.id)
        }
    }
}