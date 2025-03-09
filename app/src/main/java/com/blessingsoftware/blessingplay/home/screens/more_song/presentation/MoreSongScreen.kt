package com.blessingsoftware.blessingplay.home.screens.more_song.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blessingsoftware.blessingplay.home.presentation.component.LoadingCircularProgressIndicator
import com.blessingsoftware.blessingplay.home.presentation.component.OutlinedTextFiled

@Composable
fun MoreSongScreen(
    moreSongViewModel: MoreSongViewModel = hiltViewModel()
) {
    val moreSongState by moreSongViewModel.moreSongState.collectAsState()

    LaunchedEffect(moreSongState.moreSong) {
        if (moreSongState.moreSong != null) {
            moreSongViewModel.onAction(
                MoreSongActions.UpdateUrl("")
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        LoadingCircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            progress = moreSongState.progress,
            primaryColor = Color.White,
            secondaryColor = Color.DarkGray,
            circleRadius = 350f
        )

        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            moreSongState.message?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    text = it,
                    color = if (moreSongState.code == 200) Color.Green else Color.Red
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                if (moreSongState.moreSong == null) {
                    Column {
                        OutlinedTextFiled(
                            value = moreSongState.url ?: "",
                            onValueChange = {
                                moreSongViewModel.onAction(
                                    MoreSongActions.UpdateUrl(it)
                                )
                            },
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            colors = ButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                disabledContentColor = Color.LightGray,
                                disabledContainerColor = Color.DarkGray
                            ),
                            onClick = {
                                moreSongViewModel.onAction(
                                    MoreSongActions.UpdateMessage(null)
                                )
                                moreSongViewModel.onAction(
                                    MoreSongActions.UpdateCode(null)
                                )
                                moreSongViewModel.postUrl()
                            },
                            enabled = !moreSongState.isLoading
                        ) {
                            Text("Start extracting", fontSize = 16.sp)
                            Icon(
                                modifier = Modifier.padding(start = 5.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = "extracting",
                            )
                        }
                    }
                } else {
                    Text(
                        text = moreSongState.moreSong?.songPath ?: "",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = ButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContentColor = Color.LightGray,
                            disabledContainerColor = Color.DarkGray
                        ),
                        onClick = {
                            moreSongViewModel.downloadSongFile()
                        },
                        enabled = !moreSongState.isLoading
                    ) {
                        Text("Download song", fontSize = 16.sp)
                        Icon(
                            modifier = Modifier.padding(start = 5.dp),
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download song",
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = ButtonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black,
                            disabledContentColor = Color.LightGray,
                            disabledContainerColor = Color.DarkGray
                        ),
                        onClick = {
                            moreSongViewModel.downloadSongFile()
                        },
                    ) {
                        Text("Download more", fontSize = 16.sp)
                        Icon(
                            modifier = Modifier.padding(start = 5.dp),
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download more",
                        )
                    }
                }
            }
        }
    }
}