package com.quypham.assignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.quypham.assignment.ui.home.HomeMovieViewModel
import com.quypham.assignment.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeMovieViewModel: HomeMovieViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                val uiState by homeMovieViewModel.uiState.collectAsStateWithLifecycle()
                val detailState by homeMovieViewModel.detailUiState.collectAsStateWithLifecycle()

                HomeScreen(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    homeUIState = uiState,
                    detailUiState = detailState,
                    closeDetailScreen = {
                        homeMovieViewModel.closeMovieDetail()
                    },
                    onQueryChange = {
                        homeMovieViewModel.searchMovie(it)
                    },
                    navigateToDetail = { movieId, title, pane ->
                        homeMovieViewModel.setMovieDetail(movieId, title, pane)
                    },
                    retryFetchDetailMovie = {
                        homeMovieViewModel.fetchMovieDetail(it)
                    }
                )
            }
        }
    }
}