package com.quypham.assignment.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.quypham.assignment.api.common.onError
import com.quypham.assignment.api.common.onException
import com.quypham.assignment.api.common.onSuccess
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.common.mapToEntity
import com.quypham.assignment.data.error.NetworkErrorHandler
import com.quypham.assignment.data.repositories.movie.MovieRemoteDataSource

private const val PAGE_INDEX = 1
class MovieSearchPagingSource constructor(private val query: String,
                                          private val movieRemoteDataSource: MovieRemoteDataSource)
    : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val nextPageNumber = params.key ?: 1
        val apiResult = movieRemoteDataSource.searchMovie(query, nextPageNumber)
        var loadResult : LoadResult<Int,Movie> = LoadResult.Error(NullPointerException())
        apiResult.onSuccess {response ->
            loadResult = LoadResult.Page(
                data = response.mapToEntity(),
                prevKey = if (nextPageNumber == PAGE_INDEX) null else nextPageNumber - 1,
                nextKey = if (response.results.isEmpty()) null else nextPageNumber + 1
            )
        }.onError { code, _ ->
           loadResult = LoadResult.Error(NetworkErrorHandler.parseFromHttpErrorCode(code))
        }.onException {
           loadResult = LoadResult.Error(NetworkErrorHandler.parseFromException(it))
        }
        return loadResult
    }
}