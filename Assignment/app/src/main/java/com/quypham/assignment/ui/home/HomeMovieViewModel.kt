package com.quypham.assignment.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.quypham.assignment.data.db.Genres
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.error.Result
import com.quypham.assignment.data.repositories.MovieRepository
import com.quypham.assignment.ui.utils.AppContentType
import com.quypham.assignment.ui.utils.UiMessageParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val QUERY_DEBOUNCE_INTERVAL = 1000L
private const val QUERY_KEY_SAVED_STATE = "query"

@HiltViewModel
class HomeMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(HomeMovieUIState())
    private val _detailUiState = MutableStateFlow(DetailMovieUiState())
    private val _searchQuery = MutableStateFlow("")
    val uiState: StateFlow<HomeMovieUIState> = _uiState
    val detailUiState: StateFlow<DetailMovieUiState> = _detailUiState

    init {
        viewModelScope.launch {
            _uiState.emit(HomeMovieUIState(isLoading = true))
            initSearchEngine()
            _searchQuery.emit(savedStateHandle.get<String>(QUERY_KEY_SAVED_STATE) ?: "")
        }
    }

    private suspend fun initSearchEngine() {
        _searchQuery
            .debounce(QUERY_DEBOUNCE_INTERVAL)
            .distinctUntilChanged()
            .collectLatest { query ->
                if (query.isNotEmpty()) {
                    val queryMovies = movieRepository.searchMovie(query).cachedIn(viewModelScope)
                    val newState = _uiState.value.copy(
                        movieSource = queryMovies, isLoading = false
                    )
                    _uiState.emit(newState)
                } else {
                    val allMovies = movieRepository.getTodayTrendingMovie().cachedIn(viewModelScope)
                    val newState = _uiState.value.copy(
                        movieSource = allMovies, isLoading = false
                    )
                    _uiState.emit(newState)
                }
            }
    }

    fun searchMovie(query: String) {
        viewModelScope.launch {
            savedStateHandle[QUERY_KEY_SAVED_STATE] = query
            _searchQuery.emit(query)
        }
    }

    fun fetchMovieDetail(movieId: Int) {
        viewModelScope.launch {
            when (val result = movieRepository.getMovieDetail(movieId)) {
                is Result.Success -> {
                    result.data.let {
                        val successState = _detailUiState.value.copy(
                            id = it.id,
                            isLoading = false,
                            posterPath = it.posterPath,
                            originalTitle = it.originalTitle,
                            overview = it.overview,
                            revenue = it.revenue,
                            runTime = it.runTime,
                            genres = it.genres,
                            errorStringResId = null
                        )
                        _detailUiState.emit(successState)
                    }
                }

                is Result.Error -> {
                    val errorState = _detailUiState.value.copy(
                        errorStringResId = UiMessageParser.getErrorReasonFromErrorEntity(
                            result.error
                        ), isLoading = false
                    )
                    _detailUiState.emit(errorState)
                }
            }
        }
    }

    fun setMovieDetail(movieId: Int, movieTitle: String, contentType: AppContentType) {
        val isDetailOnlyOpen = contentType == AppContentType.SINGLE_PANE
        viewModelScope.launch {
            val initialState = _detailUiState.value.copy(
                id = movieId,
                title = movieTitle,
                isLoading = true,
                isDetailOnlyOpen = isDetailOnlyOpen,
                originalTitle = null,
                posterPath = null,
                overview = null,
                genres = arrayListOf()
            )
            _detailUiState.emit(initialState)
            fetchMovieDetail(movieId)
        }
    }

    fun closeMovieDetail() {
        viewModelScope.launch {
            if (_detailUiState.value.isDetailOnlyOpen) {
                val newState = _detailUiState.value.copy(isDetailOnlyOpen = false)
                _detailUiState.emit(newState)
            }
        }
    }
}

data class HomeMovieUIState(
    val movieSource: Flow<PagingData<Movie>> = emptyFlow(),
    val allMovie: List<Movie> = listOf(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

data class DetailMovieUiState(
    val isDetailOnlyOpen: Boolean = false,
    val isLoading : Boolean = false,
    val id: Int = -1,
    val title: String = "",
    val errorStringResId: Int? = null,
    val posterPath: String ?= null,
    val originalTitle: String? = "",
    val overview: String? = "",
    val revenue: Long = 0,
    val runTime: Int = 0,
    val genres: List<Genres> = arrayListOf()
)