package com.quypham.assignment.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.quypham.assignment.R
import com.quypham.assignment.ui.home.DetailMovieUiState
import com.quypham.assignment.ui.theme.MovieDetailAppBar

@Composable
fun DetailMovieUiScreen(
    isFullScreen: Boolean = true,
    uiState: DetailMovieUiState,
    modifier: Modifier,
    retryFetchDetailMovie : (Int) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    Column(modifier = modifier.padding(top = 10.dp)) {
        MovieDetailAppBar(uiState.title, isFullScreen) {
            onBackPressed()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (uiState.posterPath.isNullOrEmpty()) {
                Image(
                    modifier = Modifier
                        .height(220.dp)
                        .width(180.dp)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .clip(CardDefaults.outlinedShape),
                    painter = painterResource(id = R.drawable.placeholder),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                var isImageLoading by remember { mutableStateOf(false) }

                val painter = rememberAsyncImagePainter(
                    model = "https://image.tmdb.org/t/p/w154" + uiState.posterPath,
                )

                isImageLoading = when (painter.state) {
                    is AsyncImagePainter.State.Loading -> true
                    else -> false
                }

                Image(
                    modifier = Modifier
                        .height(220.dp)
                        .width(180.dp)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .clip(CardDefaults.outlinedShape),
                    painter = painter,
                    contentDescription = "Poster Image",
                    contentScale = ContentScale.FillBounds,
                )

                if (isImageLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        if (uiState.errorStringResId == null) {
            LazyRow(modifier = Modifier.padding(7.dp)) {
                items(count = uiState.genres.size) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp)
                    ) {
                        Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 5.dp, bottom = 5.dp),
                            text = uiState.genres[it].name,
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            Box(modifier = Modifier
                .padding(5.dp)
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = uiState.overview ?: "",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = stringResource(id = uiState.errorStringResId),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                )

                Button(
                    onClick = {
                        retryFetchDetailMovie(uiState.id)
                    },
                    content = {
                        Text(text = stringResource(id = R.string.retry))
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
        }
    }
}

