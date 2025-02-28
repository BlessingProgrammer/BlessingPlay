package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.presentation.Screen
import com.blessingsoftware.blessingplay.home.presentation.component.ActionIcon
import com.blessingsoftware.blessingplay.home.presentation.component.HomeDialog
import com.blessingsoftware.blessingplay.home.presentation.component.OutlinedTextFiled
import com.blessingsoftware.blessingplay.home.presentation.component.SwipeAbleItemWithActions
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterial3Api
@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    navController: NavController
) {
    val playlistState by playlistViewModel.playlistState.collectAsState()

    val listState = rememberLazyListState()
    val refreshState = rememberPullToRefreshState()

    LaunchedEffect(true) {
        playlistViewModel.playlistFlow.collectLatest { saved ->
            if (saved) {
                playlistViewModel.onAction(
                    PlaylistActions.UpdateIsAddDialog(false)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdatePlaylistName("")
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdatePlaylistThumbnail("")
                )
                playlistViewModel.onAction(
                    PlaylistActions.LoadPlaylists
                )
            } else {

            }
        }
    }

    LaunchedEffect(playlistState.playlistSelected) {
        playlistState.playlistSelected?.let { playlistSelected ->
            playlistViewModel.onAction(
                PlaylistActions.UpdatePlaylistName(playlistSelected.name)
            )
            playlistViewModel.onAction(
                PlaylistActions.UpdatePlaylistThumbnail(
                    playlistSelected.thumbnail ?: ""
                )
            )
        } ?: run {
            playlistViewModel.onAction(
                PlaylistActions.UpdatePlaylistName("")
            )
            playlistViewModel.onAction(
                PlaylistActions.UpdatePlaylistThumbnail("")
            )
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Playlist",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    playlistViewModel.onAction(
                        PlaylistActions.UpdateIsAddDialog(true)
                    )
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Playlist"
                )
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = playlistState.pullLoading,
            onRefresh = {
                playlistViewModel.onAction(
                    PlaylistActions.PullToRefresh
                )
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (playlistState.dataLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                } else {
                    if (playlistState.playlists.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No playlists available",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    } else {
                        itemsIndexed(
                            items = playlistState.playlists,
                            key = { _, playlist -> playlist.id }
                        ) { _, playlist ->
                            SwipeAbleItemWithActions(
                                isRevealed = playlistState.revealedItemId == playlist.id,
                                onExpanded = {
                                    playlistViewModel.onAction(
                                        PlaylistActions.UpdateRevealedItemId(playlist.id)
                                    )
                                },
                                onCollapsed = {
                                    if (playlistState.revealedItemId == playlist.id) {
                                        playlistViewModel.onAction(
                                            PlaylistActions.UpdateRevealedItemId(null)
                                        )
                                    }
                                },
                                actions = {
                                    ActionIcon(
                                        onClick = {

                                        },
                                        backgroundColor = Color.LightGray,
                                        icon = Icons.Default.PlayArrow,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(60.dp)
                                    )
                                    ActionIcon(
                                        onClick = {
                                            playlistViewModel.onAction(
                                                PlaylistActions.UpdateIsUpdateDialog(true)
                                            )
                                            playlistViewModel.onAction(
                                                PlaylistActions.UpdatePlaylistSelected(playlist)
                                            )
                                        },
                                        backgroundColor = Color.Magenta,
                                        icon = Icons.Default.Edit,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(60.dp)
                                    )
                                    ActionIcon(
                                        onClick = {
                                            playlistViewModel.onAction(
                                                PlaylistActions.UpdateIsDeleteDialog(true)
                                            )
                                            playlistViewModel.onAction(
                                                PlaylistActions.UpdatePlaylistSelected(playlist)
                                            )
                                        },
                                        backgroundColor = Color.Red,
                                        icon = Icons.Default.Delete,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(60.dp)
                                    )
                                }
                            ) {
                                PlaylistItem(
                                    playlist = playlist,
                                    onClick = {
                                        if (playlistState.revealedItemId != null) {
                                            playlistViewModel.onAction(
                                                PlaylistActions.UpdateRevealedItemId(null)
                                            )
                                        } else {
                                            navController.navigate(
                                                Screen.PlaylistSongsScreen(
                                                    playlistId = playlist.id,
                                                    name = playlist.name
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        HomeDialog(
            isMultiButton = true,
            onVisible = playlistState.isAddDialog,
            onSubmit = {
                playlistViewModel.onAction(
                    PlaylistActions.SavePlaylist
                )
            },
            onDismiss = {
                playlistViewModel.onAction(
                    PlaylistActions.UpdateIsAddDialog(false)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdateRevealedItemId(null)
                )
            },
            buttonBackgroundColor = Color.Green,
            isEnabled = playlistState.playlistName.isNotBlank(),
            title = "Add"
        ) {
            PlaylistAddAndUpdateDialog(
                name = playlistState.playlistName,
                onChangeName = {
                    playlistViewModel.onAction(
                        PlaylistActions.UpdatePlaylistName(it)
                    )
                },
                thumbnail = playlistState.playlistThumbnail,
                onChangeThumbnail = {
                    playlistViewModel.onAction(
                        PlaylistActions.UpdatePlaylistThumbnail(it)
                    )
                }
            )
        }

        HomeDialog(
            isMultiButton = true,
            onVisible = playlistState.isUpdateDialog,
            onSubmit = {
                playlistViewModel.onAction(
                    PlaylistActions.UpdatePlaylist
                )
            },
            onDismiss = {
                playlistViewModel.onAction(
                    PlaylistActions.UpdateIsUpdateDialog(false)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdatePlaylistSelected(null)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdateRevealedItemId(null)
                )
            },
            isEnabled = playlistState.playlistName.isNotBlank(),
            title = "Update"
        ) {
            PlaylistAddAndUpdateDialog(
                name = playlistState.playlistName,
                onChangeName = {
                    playlistViewModel.onAction(
                        PlaylistActions.UpdatePlaylistName(it)
                    )
                },
                thumbnail = playlistState.playlistThumbnail,
                onChangeThumbnail = {
                    playlistViewModel.onAction(
                        PlaylistActions.UpdatePlaylistThumbnail(it)
                    )
                }
            )
        }

        HomeDialog(
            isMultiButton = true,
            onVisible = playlistState.isDeleteDialog,
            onSubmit = {
                playlistViewModel.onAction(
                    PlaylistActions.DeletePlaylist
                )
            },
            onDismiss = {
                playlistViewModel.onAction(
                    PlaylistActions.UpdateIsDeleteDialog(false)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdatePlaylistSelected(null)
                )
                playlistViewModel.onAction(
                    PlaylistActions.UpdateRevealedItemId(null)
                )
            },
            title = "Delete",
            buttonBackgroundColor = Color.Red
        ) {
            PlaylistDeleteDialog(
                deleteName = playlistState.playlistSelected?.name ?: ""
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.thumbnail.ifBlank { R.drawable.music },
            contentDescription = playlist.id.toString(),
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .drawBehind {
                    val strokeWidth = 0.5.dp.toPx()
                    val color = Color.Gray
                    drawLine(
                        color = color,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = playlist.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${playlist.songCount} ${if (playlist.songCount > 1) "songs" else "song"}",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlaylistAddAndUpdateDialog(
    name: String,
    onChangeName: (String) -> Unit,
    thumbnail: String?,
    onChangeThumbnail: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onChangeThumbnail(it.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Playlist name:",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextFiled(
            value = name,
            onValueChange = { onChangeName(it) }
        )
        Text(
            text = "Thumbnail:",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 15.dp)
                .padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") }
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (!thumbnail.isNullOrBlank()) {
                AsyncImage(
                    model = thumbnail,
                    contentDescription = "Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = "Click to select image", color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun PlaylistDeleteDialog(
    deleteName: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = deleteName,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Once deleted, it cannot be restored. Confirm?",
            fontSize = 15.sp,
        )
    }
}