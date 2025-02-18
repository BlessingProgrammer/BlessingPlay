package com.blessingsoftware.blessingplay.home.screens.library.song_list.presentation

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.home.presentation.component.ActionIcon
import com.blessingsoftware.blessingplay.home.presentation.component.SwipeAbleItemWithActions

@ExperimentalMaterial3Api
@Composable
fun SongListScreen(
    songListViewModel: SongListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val songListState by songListViewModel.songListState.collectAsState()

    val revealedItemId by songListViewModel.revealedItemId.collectAsState()

    val isLoading by songListViewModel.isLoading.collectAsState()

    val listState = rememberLazyListState()

    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = refreshState,
        isRefreshing = isLoading,
        onRefresh = {
            songListViewModel.pullToRefresh()
        }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            songListState.forEach { (header, songs) ->
                item {
                    Text(
                        text = header.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(start = 10.dp)
                            .padding(top = 7.dp)
                            .padding(bottom = 3.dp)
                    )
                }

                itemsIndexed(
                    items = songs,
                    key = { _, song -> song.id }
                ) { _, song ->
                    SwipeAbleItemWithActions(
                        isRevealed = revealedItemId == song.id.toString(),
                        onExpanded = { songListViewModel.setRevealedItemId(song.id.toString()) },
                        onCollapsed = {
                            if (revealedItemId == song.id.toString()) {
                                songListViewModel.setRevealedItemId(null)
                            }
                        },
                        actions = {
                            ActionIcon(
                                onClick = {
                                    songListViewModel.deleteSong(song)
                                    songListViewModel.setRevealedItemId(null)
                                    Toast.makeText(
                                        context, "${song.title} deleted ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                backgroundColor = Color.Red,
                                icon = Icons.Default.Delete,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                            )

                            ActionIcon(
                                onClick = {
                                    songListViewModel.setRevealedItemId(null)
                                },
                                backgroundColor = Color.Magenta,
                                icon = Icons.Default.Edit,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                            )

                            ActionIcon(
                                onClick = {
                                    songListViewModel.setRevealedItemId(null)
                                },
                                backgroundColor = Color.LightGray,
                                icon = Icons.Default.MoreVert,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                            )
                        }
                    ) {
                        ListSongItem(
                            song = song,
                            onClick = {
                                if (revealedItemId !== null) {
                                    songListViewModel.setRevealedItemId(null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListSongItem(
    song: Song,
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
}