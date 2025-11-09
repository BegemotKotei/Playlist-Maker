package com.example.playlistmaker.search.presentation.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    tracks: List<TrackUI>,
    isHistory: Boolean,
    onClearHistory: () -> Unit,
    onTrackClick: (TrackUI) -> Unit,
    isLoading: Boolean = false,
    isErrorConnection: Boolean = false,
    searchFinished: Boolean = true,
    isNothingFound: Boolean = false,
    navigateToPlayer: (TrackUI) -> Unit,
    onSearch: (String) -> Unit = {}
) {
    var pendingTrackToPlay by remember { mutableStateOf<TrackUI?>(null) }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            delay(2000L)
            onSearch(query)
        }
    }

    val showLoading = isLoading
    val showError = query.isNotBlank() && searchFinished && isErrorConnection
    val showNothingFound =
        query.isNotBlank() && searchFinished && !isErrorConnection && isNothingFound
    val showInitialState = query.isBlank() && tracks.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.searchDisp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 16.dp)
                .align(Alignment.Start)
        )

        CustomSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isHistory && tracks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.your_search),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp, bottom = 8.dp)
            )
        }

        when {
            showLoading -> {
                Spacer(modifier = Modifier.height(108.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            showError -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorPlaceholder(
                        imageRes = R.drawable.no_internet,
                        title = stringResource(R.string.wrong_title),
                        subtitle = stringResource(R.string.wrong),
                        onRetry = { onSearch(query) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            showNothingFound -> {
                ErrorPlaceholder(
                    imageRes = R.drawable.no_music,
                    title = stringResource(R.string.nothing),
                    subtitle = "",
                    onRetry = null
                )
            }

            showInitialState -> {

            }

            else -> {
                val displayedTracks = if (isHistory) {
                    if (tracks.size > 10) tracks.takeLast(10) else tracks
                } else {
                    if (tracks.size > 15) tracks.take(15) else tracks
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        itemsIndexed(displayedTracks) { _, track ->
                            TrackItem(track = track) {
                                onTrackClick(track)
                                pendingTrackToPlay = track
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isHistory && tracks.isNotEmpty()) {
            Button(
                onClick = onClearHistory,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.clear_history),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }

    LaunchedEffect(pendingTrackToPlay) {
        pendingTrackToPlay?.let { track ->
            navigateToPlayer(track)
            pendingTrackToPlay = null
        }
    }
}

@Composable
fun ErrorPlaceholder(
    imageRes: Int,
    title: String,
    subtitle: String,
    onRetry: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        onRetry?.let {
            Button(
                onClick = it,
                modifier = Modifier
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(stringResource(R.string.restore))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val sampleTracks = listOf(
        TrackUI(
            trackName = "Sample Track",
            artistName = "Sample Artist",
            artworkUrl100 = "https://example.com/artwork.jpg",
            trackTimeMillis = "180000",
            previewUrl = "https://example.com/preview.mp3",
            collectionName = "Sample Collection",
            trackId = "1",
            releaseDate = "2022-01-01",
            primaryGenreName = "Sample Genre",
            isLiked = false,
            country = "Sample Country",
        )
    )

    MaterialTheme {
        Surface {
            SearchScreen(
                query = "Imagine",
                onQueryChange = {},
                onClearQuery = {},
                tracks = sampleTracks,
                isHistory = true,
                onClearHistory = {},
                onTrackClick = {},
                onSearch = {},
                navigateToPlayer = {},
            )
        }
    }
}