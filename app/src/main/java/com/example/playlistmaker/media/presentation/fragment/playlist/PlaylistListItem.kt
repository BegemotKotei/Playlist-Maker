package com.example.playlistmaker.media.presentation.fragment.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

@Composable
fun PlaylistListItem(
    playlist: PlayListUI,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playlist.roadToFileImage.takeIf { it.isNotBlank() }
                        ?: R.drawable.placeholder_ic)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = if (playlist.roadToFileImage.isBlank()) ContentScale.Fit else ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = playlist.namePlayList,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "${playlist.count} треков",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}