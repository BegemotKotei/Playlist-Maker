package com.example.playlistmaker.search.presentation.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.playlistmaker.search.presentation.models.TrackUI

@Composable
fun TrackItem(track: TrackUI, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(1.dp, 4.dp, 8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(track.artworkUrl100),
            contentDescription = "Track artwork",
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = track.trackTimeMillis.toFormattedTime(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to details, fk yeah it's work",
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

fun String.toFormattedTime(): String {
    val millis = this.toLongOrNull() ?: 0L
    val minutes = millis / 60000
    val seconds = (millis % 60000) / 1000
    return "%d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun TrackItemPreview() {
    val sampleTrack = TrackUI(
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

    MaterialTheme {
        Surface {
            TrackItem(track = sampleTrack, onClick = {})
        }
    }
}
