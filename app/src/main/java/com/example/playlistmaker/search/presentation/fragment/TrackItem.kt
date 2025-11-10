package com.example.playlistmaker.search.presentation.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
            .padding(vertical = 10.dp),
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = " • ${track.trackTimeMillis.toFormattedTime()}",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    maxLines = 1,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to details",
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(start = 8.dp)
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
private fun TrackItemPreview() {
    val sampleTrack = TrackUI(
        trackName = "Длинное название трека, которое должно обрезаться",
        artistName = "Очень-очень длинное имя исполнителя, которое не помещается на экране",
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