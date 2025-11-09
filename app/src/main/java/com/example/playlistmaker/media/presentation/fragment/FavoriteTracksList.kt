package com.example.playlistmaker.media.presentation.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.presentation.fragment.TrackItem
import com.example.playlistmaker.search.presentation.models.TrackUI

@Composable
fun FavoriteTracksList(
    tracks: List<TrackUI>,
    onTrackClick: (TrackUI) -> Unit
) {
    val ysDisplayMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    )

    if (tracks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 106.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_music),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.no_mediateka),
                fontSize = 19.sp,
                style = ysDisplayMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )

        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 13.dp)
        ) {
            items(tracks) { track ->
                TrackItem(track = track, onClick = { onTrackClick(track) })
            }
        }
    }
}