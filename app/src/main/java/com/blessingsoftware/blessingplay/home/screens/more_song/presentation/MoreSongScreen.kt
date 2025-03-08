package com.blessingsoftware.blessingplay.home.screens.more_song.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blessingsoftware.blessingplay.home.presentation.component.LoadingCircularProgressIndicator

@Composable
fun MoreSongScreen(
    moreSongViewModel: MoreSongViewModel = hiltViewModel()
) {
    val moreSongState by moreSongViewModel.moreSongState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        TextField(
            value = moreSongState.url ?: "",
            onValueChange = {
                moreSongViewModel.onAction(
                    MoreSongActions.UpdateUrl(it)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Url") },
            placeholder = { Text(text = "Enter your song link") },
        )
        Button(
            onClick = {
                moreSongViewModel.postUrl()
            }
        ) { Text(text = "Handle") }

        moreSongState.moreSong?.let {
            Text(text = it.songPath)
        }

        LoadingCircularProgressIndicator(
            modifier = Modifier.size(500.dp),
            progress = moreSongState.progress,
            primaryColor = Color.White,
            secondaryColor = Color.DarkGray,
            circleRadius = 230f
        )
    }
}