package com.quypham.assignment.ui.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.window.layout.DisplayFeature
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.quypham.assignment.R
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.error.ErrorEntity
import com.quypham.assignment.ui.detail.DetailMovieUiScreen
import com.quypham.assignment.ui.theme.AppSearchBar
import com.quypham.assignment.ui.utils.AppContentType
import com.quypham.assignment.ui.utils.AppNavigationType
import com.quypham.assignment.ui.utils.UiMessageParser
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieScreen(
    contentType: AppContentType,
    homeMovieUIState: HomeMovieUIState,
    detailMovieUiState: DetailMovieUiState,
    navigationType: AppNavigationType,
    onQueryChange: (String) -> Unit,
    displayFeatures: List<DisplayFeature>,
    closeDetailScreen: () -> Unit,
    retryFetchDetailMovie : (Int) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * When moving from LIST_AND_DETAIL page to LIST page clear the selection and user should see LIST screen.
     */
    LaunchedEffect(key1 = contentType) {
        if (contentType == AppContentType.SINGLE_PANE && !detailMovieUiState.isDetailOnlyOpen) {
            closeDetailScreen()
        }
    }

    val movieLazyListState = rememberLazyListState()

    if (contentType == AppContentType.DUAL_PANE) {
        TwoPane(
            first = {
                MovieList(
                    movieSource = homeMovieUIState.movieSource,
                    navigateToDetail = navigateToDetail,
                    onQueryChange = onQueryChange,
                    movieLazyListState = movieLazyListState
                )
            },
            second = {
                DetailMovieUiScreen(uiState = detailMovieUiState, modifier = modifier, retryFetchDetailMovie = retryFetchDetailMovie)
            },
            strategy = HorizontalTwoPaneStrategy(splitFraction = 0.5f, gapWidth = 16.dp),
            displayFeatures = displayFeatures
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            AppSinglePaneContent(
                homeMovieUIState = homeMovieUIState,
                modifier = Modifier.fillMaxSize(),
                closeDetailScreen = closeDetailScreen,
                detailMovieUiState = detailMovieUiState,
                navigateToDetail = navigateToDetail,
                onQueryChange = onQueryChange,
                movieLazyListState = movieLazyListState,
                retryFetchDetailMovie = retryFetchDetailMovie
            )
        }
    }
}

@Composable
fun AppSinglePaneContent(
    homeMovieUIState: HomeMovieUIState,
    detailMovieUiState: DetailMovieUiState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    onQueryChange: (String) -> Unit,
    movieLazyListState: LazyListState,
    retryFetchDetailMovie : (Int) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit
) {
    if (detailMovieUiState.id > 0 && detailMovieUiState.isDetailOnlyOpen) {
        BackHandler {
            closeDetailScreen()
        }
        DetailMovieUiScreen(uiState = detailMovieUiState,retryFetchDetailMovie = retryFetchDetailMovie, modifier = modifier) {
            closeDetailScreen()
        }
    } else {
        MovieList(
            movieSource = homeMovieUIState.movieSource,
            modifier = modifier,
            navigateToDetail = navigateToDetail,
            onQueryChange = onQueryChange,
            movieLazyListState = movieLazyListState
        )
    }
}

@Composable
fun MovieList(
    movieSource: Flow<PagingData<Movie>>,
    modifier: Modifier = Modifier,
    movieLazyListState: LazyListState,
    navigateToDetail: (Int, String, AppContentType) -> Unit,
    onQueryChange: (String) -> Unit
) {

    val movieLazyPagingItems = movieSource.collectAsLazyPagingItems()

    Box(modifier = modifier) {
        AppSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onQueryChanged = onQueryChange
        )
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 88.dp),
            state = movieLazyListState
        ) {
            items(
                movieLazyPagingItems.itemCount,
                key = movieLazyPagingItems.itemKey { it.id }
            ) { index ->
                val movie = movieLazyPagingItems[index]

                if (movie != null) {
                    MovieItem(modifier, movie, navigateToDetail = { movieId ->
                        navigateToDetail(movieId, movie.title, AppContentType.SINGLE_PANE)
                    })
                } else {
                    MoviePlaceHolder()
                }
            }

            item {
                val loadState =
                    movieLazyPagingItems.loadState.mediator ?: movieLazyPagingItems.loadState.source
                //source for trending movies
                if (loadState.refresh == LoadState.Loading) {
                    Column(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = stringResource(id = R.string.fetch_movie),
                            color = MaterialTheme.colorScheme.primary
                        )

                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (loadState.append == LoadState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error) {
                    val isPaginatingError =
                        (loadState.append is LoadState.Error) || movieLazyPagingItems.itemCount > 1
                    val error = if (loadState.append is LoadState.Error)
                        (loadState.append as LoadState.Error).error
                    else
                        (loadState.refresh as LoadState.Error).error

                    val errorModifier = if (isPaginatingError) {
                        Modifier.padding(8.dp)
                    } else {
                        Modifier.fillParentMaxSize()
                    }
                    Column(
                        modifier = errorModifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = error.message ?: stringResource(id = UiMessageParser.getErrorReasonFromErrorEntity(error as ErrorEntity)),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Button(
                            onClick = {
                                movieLazyPagingItems.refresh()
                            },
                            content = {
                                Text(text = stringResource(id = R.string.retry))
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                } else if (loadState.append.endOfPaginationReached && movieLazyPagingItems.itemCount == 0) {
                    //No movie found error cases
                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = stringResource(id = R.string.no_movies_found),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}


@OptIn(
    ExperimentalFoundationApi::class,
)
@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: Movie,
    navigateToDetail: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { navigateToDetail(movie.id) },
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var isImageLoading by remember { mutableStateOf(false) }

            val painter = rememberAsyncImagePainter(
                model = "https://image.tmdb.org/t/p/w154" + movie.posterUrl,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )

            isImageLoading = when (painter.state) {
                is AsyncImagePainter.State.Loading -> true
                else -> false
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .height(120.dp)
                        .width(90.dp)
                        .clip(CardDefaults.shape),
                    painter = painter,
                    contentDescription = "Poster Image",
                    contentScale = ContentScale.FillBounds,
                )
            }
            Text(
                modifier = Modifier
                    .padding(vertical = 18.dp, horizontal = 8.dp),
                text = movie.title
            )
        }
    }
}

@Composable
fun MoviePlaceHolder() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .height(120.dp)
                .width(90.dp)
                .clip(CardDefaults.shape),
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
        )

        CircularProgressIndicator(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 3.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 8.dp),
            text = "..."
        )
    }
}

