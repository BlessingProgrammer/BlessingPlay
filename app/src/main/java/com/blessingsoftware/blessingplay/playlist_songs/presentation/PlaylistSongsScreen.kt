package com.blessingsoftware.blessingplay.playlist_songs.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.Screen
import com.blessingsoftware.blessingplay.home.presentation.component.ActionIcon
import com.blessingsoftware.blessingplay.home.presentation.component.SwipeAbleItemWithActions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.blessingsoftware.blessingplay.home.presentation.component.HomeDialog
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@Composable
fun PlaylistSongsScreen(
    playlistSongsViewModel: PlaylistSongsViewModel = hiltViewModel(),
    navController: NavController,
    playlistId: Long,
    name: String,
    thumbnail: String?
) {
    val playlistSongsState by playlistSongsViewModel.playlistSongsState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(true) {
        playlistSongsViewModel.loadSongWithPositionLists(playlistId)
    }

    LaunchedEffect(true) {
        playlistSongsViewModel.playlistSongFlow.collectLatest { deleted ->
            if (deleted) {
                playlistSongsViewModel.onAction(
                    PlaylistSongsActions.UpdateIsDeleteDialog(false)
                )
                playlistSongsViewModel.onAction(
                    PlaylistSongsActions.UpdateSongSelected(null)
                )
                playlistSongsViewModel.loadSongWithPositionLists(playlistId)
            }
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                ActionIcon(
                    onClick = {
                        navController.popBackStack()
                    },
                    backgroundColor = Color.Transparent,
                    icon = Icons.Filled.ArrowBack,
                    modifier = Modifier
                        .width(35.dp)
                        .align(Alignment.CenterStart)
                )
                Text(
                    text = name,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (playlistSongsState.songWithPositionLists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs available",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                item {
                    AsyncImage(
                        model = thumbnail?.ifBlank { R.drawable.music },
                        contentDescription = playlistId.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                itemsIndexed(
                    items = playlistSongsState.songWithPositionLists,
                    key = { _, song -> song.id }
                ) { index, song ->
                    SwipeAbleItemWithActions(
                        isRevealed = playlistSongsState.revealedItemId == song.id,
                        onExpanded = {
                            playlistSongsViewModel.onAction(
                                PlaylistSongsActions.UpdateRevealedItemId(song.id)
                            )
                        },
                        onCollapsed = {
                            if (playlistSongsState.revealedItemId == song.id) {
                                playlistSongsViewModel.onAction(
                                    PlaylistSongsActions.UpdateRevealedItemId(null)
                                )
                            }
                        },
                        actions = {
                            ActionIcon(
                                onClick = {
                                    playlistSongsViewModel.onAction(
                                        PlaylistSongsActions.UpdateIsDeleteDialog(true)
                                    )
                                    playlistSongsViewModel.onAction(
                                        PlaylistSongsActions.UpdateSongSelected(song)
                                    )
                                },
                                backgroundColor = Color.Red,
                                icon = Icons.Default.Delete,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                            )
                            ActionIcon(
                                onClick = {
                                    navController.navigate(Screen.Home)
                                },
                                backgroundColor = Color.LightGray,
                                icon = Icons.Default.PlayArrow,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                            )
                        }
                    ) {
                        ListSongWithPositionItem(
                            index = index,
                            song = song,
                            onClick = {
                                if (playlistSongsState.revealedItemId != null) {
                                    playlistSongsViewModel.onAction(
                                        PlaylistSongsActions.UpdateRevealedItemId(null)
                                    )
                                } else {

                                }
                            },
                            playlistSongsViewModel = playlistSongsViewModel,
                            playlistId = playlistId
                        )
                    }
                }
            }
        }
    }

    HomeDialog(
        isMultiButton = true,
        onVisible = playlistSongsState.isDeleteDialog,
        onSubmit = {
            playlistSongsViewModel.deleteSongPosition(playlistId)
        },
        onDismiss = {
            playlistSongsViewModel.onAction(
                PlaylistSongsActions.UpdateIsDeleteDialog(false)
            )
            playlistSongsViewModel.onAction(
                PlaylistSongsActions.UpdateSongSelected(null)
            )
            playlistSongsViewModel.onAction(
                PlaylistSongsActions.UpdateRevealedItemId(null)
            )
        },
        title = "Delete",
        buttonBackgroundColor = Color.Red
    ) {
        SongPositionDeleteDialog(
            deleteName = playlistSongsState.songSelected?.title ?: ""
        )
    }
}

@Composable
private fun ListSongWithPositionItem(
    index: Int,
    playlistId: Long,
    song: Song,
    onClick: () -> Unit,
    playlistSongsViewModel: PlaylistSongsViewModel,
    modifier: Modifier = Modifier
) {
    val itemHeightPx = with(LocalDensity.current) { 60.dp.toPx() }
    var dragOffset by remember { mutableStateOf(0f) }
    val isDragging = dragOffset != 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        playlistSongsViewModel.onAction(
                            PlaylistSongsActions.UpdateOldPosition(song.position)
                        )
                        dragOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        dragOffset += dragAmount.y
                        val offsetItems = (dragOffset / itemHeightPx).roundToInt()
                        val newIndex = (index + offsetItems).coerceIn(
                            0,
                            playlistSongsViewModel.playlistSongsState.value.songWithPositionLists.size - 1
                        )
                        if (newIndex != index) {
                            playlistSongsViewModel.moveSongPositionLocally(
                                songId = song.id,
                                newPosition = newIndex.toLong()
                            )
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        playlistSongsViewModel.updateSongPosition(
                            playlistId = playlistId,
                            songId = song.id,
                            newPosition = (index + (dragOffset / itemHeightPx).roundToInt()).toLong() + 1
                        )

                        dragOffset = 0f
                    },
                    onDragCancel = {
                        dragOffset = 0f
                    }
                )
            }
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.albumArt ?: R.drawable.music,
                contentDescription = song.artistId.toString(),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = song.artist,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isDragging) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
private fun SongPositionDeleteDialog(
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
            text = "Remove song from playlist. Confirm?",
            fontSize = 15.sp,
        )
    }
}

