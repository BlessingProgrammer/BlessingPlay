package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.home.presentation.component.ActionIcon
import com.blessingsoftware.blessingplay.home.presentation.component.SwipeAbleItemWithActions

@Composable
fun PlayListScreen(
    playListViewModel: PlayListViewModel = hiltViewModel()
) {
    val playlistListState by playListViewModel.playlistListState.collectAsState()

    val revealedItemId by playListViewModel.revealedItemId.collectAsState()

    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            Text(
                text = "Playlist",
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            itemsIndexed(
                items = playlistListState,
                key = { _, playlist -> playlist.id }
            ){ _, playlist ->
//                SwipeAbleItemWithActions(
//                    isRevealed = revealedItemId == playlist.id,
//                    onExpanded = { songListViewModel.setRevealedItemId(song.id) },
//                    onCollapsed = {
//                        if (revealedItemId == song.id) {
//                            songListViewModel.setRevealedItemId(null)
//                        }
//                    },
//                    actions = {
//                        ActionIcon(
//                            onClick = {
//                                songListViewModel.setSongStateForDelete(song)
//                            },
//                            backgroundColor = Color.Red,
//                            icon = Icons.Default.Delete,
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .width(60.dp)
//                        )
//                        ActionIcon(
//                            onClick = {
//                                songListViewModel.setSongStateForUpdate(song)
//                            },
//                            backgroundColor = Color.Magenta,
//                            icon = Icons.Default.Edit,
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .width(60.dp)
//                        )
//                        ActionIcon(
//                            onClick = {
//                                extendDialogVisible.value = true
//                            },
//                            backgroundColor = Color.LightGray,
//                            icon = Icons.Default.MoreVert,
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .width(60.dp)
//                        )
//                    }
//                ) {
//
//                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.thumbnail ?: R.drawable.music,
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

//            Text(
//                text = song.artist,
//                fontSize = 12.sp,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
        }
    }
}