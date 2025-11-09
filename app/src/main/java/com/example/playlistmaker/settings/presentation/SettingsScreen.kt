package com.example.playlistmaker.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val isDarkTheme by viewModel.theme.observeAsState(initial = false)

    SettingsTheme(darkTheme = isDarkTheme) {
        SettingsContent(
            isDarkTheme = isDarkTheme,
            onThemeSwitchChanged = { isChecked ->
                viewModel.updateTheme(isChecked)
            },
            onShareClick = onShareClick,
            onSupportClick = onSupportClick,
            onTermsClick = onTermsClick
        )
    }
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeSwitchChanged: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val customFont = FontFamily(Font(R.font.ys_display_regular))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeSwitchChanged(!isDarkTheme) }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.dark_mode),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontFamily = customFont
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeSwitchChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        SettingsItem(stringResource(R.string.share_app), R.drawable.share, onShareClick)
        SettingsItem(stringResource(R.string.write_support), R.drawable.support, onSupportClick)
        SettingsItem(
            stringResource(R.string.user_agreement),
            R.drawable.arrow_forward,
            onTermsClick
        )
    }
}


@Composable
fun SettingsItem(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    val customFont = FontFamily(Font(R.font.ys_display_regular))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontFamily = customFont
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary

        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsContentPreview() {
    SettingsContent(
        isDarkTheme = false,
        onThemeSwitchChanged = {},
        onShareClick = {},
        onSupportClick = {},
        onTermsClick = {}
    )
}
