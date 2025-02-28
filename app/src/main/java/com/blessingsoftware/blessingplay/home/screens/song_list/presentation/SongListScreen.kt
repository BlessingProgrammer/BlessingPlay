package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.formatDateModified
import com.blessingsoftware.blessingplay.core.presentation.utils.formatDuration
import com.blessingsoftware.blessingplay.core.presentation.utils.formatSize
import com.blessingsoftware.blessingplay.home.presentation.component.ActionIcon
import com.blessingsoftware.blessingplay.home.presentation.component.HomeDialog
import com.blessingsoftware.blessingplay.home.presentation.component.OutlinedTextFiled
import com.blessingsoftware.blessingplay.home.presentation.component.SwipeAbleItemWithActions
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun SongListScreen(
    songListViewModel: SongListViewModel = hiltViewModel()
) {
    val songListState by songListViewModel.songListState.collectAsState()

    val currentQueryIndex = remember { mutableIntStateOf(-1) }

    val listState = rememberLazyListState()
    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(songListState.updateIndex) {
        songListState.updateIndex?.let {
            listState.animateScrollToItem(
                index = it,
                scrollOffset = 0
            )
        }
    }

    LaunchedEffect(songListState.searchQuery) {
        if (songListState.searchQuery.isBlank()) {
            songListViewModel.onAction(
                SongListActions.UpdateIndexListSearching(emptyList())
            )
            if (listState.firstVisibleItemIndex != 0) {
                listState.scrollToItem(0)
            }
        } else songListViewModel.onAction(
            SongListActions.SearchingSong
        )
    }

    LaunchedEffect(songListState.songSelected) {
        songListState.songSelected?.let {
            songListViewModel.onAction(
                SongListActions.UpdateSongTitle(it.title)
            )
            songListViewModel.onAction(
                SongListActions.UpdateSongArtist(it.artist)
            )
        }
    }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = "Song list",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                UnderlinedSearchTextField(
                    value = songListState.searchQuery,
                    onValueChange = {
                        songListViewModel.onAction(
                            SongListActions.UpdateSearchQuery(it)
                        )
                    },
                    placeholder = "Search songs...",
                    modifier = Modifier.fillMaxWidth(),
                    onImeAction = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index = songListState.indexListSearching.first()
                            )
                            currentQueryIndex.value = 0
                        }
                    },
                    onUp = {
                        coroutineScope.launch {
                            if (songListState.indexListSearching.isNotEmpty()) {
                                val prevIndex = if (currentQueryIndex.value > 0) {
                                    currentQueryIndex.value - 1
                                } else {
                                    songListState.indexListSearching.size - 1
                                }

                                currentQueryIndex.value = prevIndex

                                listState.animateScrollToItem(
                                    index = songListState.indexListSearching[prevIndex]
                                )
                            }
                        }
                    },
                    onDown = {
                        coroutineScope.launch {
                            if (songListState.indexListSearching.isNotEmpty()) {
                                val nextIndex =
                                    if (currentQueryIndex.value < songListState.indexListSearching.size - 1) {
                                        currentQueryIndex.value + 1
                                    } else {
                                        0
                                    }

                                currentQueryIndex.value = nextIndex

                                listState.animateScrollToItem(
                                    index = songListState.indexListSearching[nextIndex],
                                    scrollOffset = 0
                                )
                            }
                        }
                    },
                    onClear = {
                        songListViewModel.onAction(
                            SongListActions.UpdateSearchQuery("")
                        )
                        currentQueryIndex.value = -1
                    },
                    result = songListState.indexListSearching.size,
                    currentIndex = currentQueryIndex.value + 1
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = songListState.pullLoading,
            onRefresh = {
                songListViewModel.onAction(
                    SongListActions.PullToRefresh
                )
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (songListState.dataLoading) {
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
                    if (songListState.songLists.isEmpty()) {
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
                        songListState.songLists.forEach { (header, songs) ->
                            item {
                                Text(
                                    text = header.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, top = 7.dp, bottom = 3.dp)
                                )
                            }
                            itemsIndexed(
                                items = songs,
                                key = { _, song -> song.id }
                            ) { _, song ->
                                SwipeAbleItemWithActions(
                                    isRevealed = songListState.revealedItemId == song.id,
                                    onExpanded = {
                                        songListViewModel.onAction(
                                            SongListActions.UpdateRevealedItemId(song.id)
                                        )
                                    },
                                    onCollapsed = {
                                        if (songListState.revealedItemId == song.id) {
                                            songListViewModel.onAction(
                                                SongListActions.UpdateRevealedItemId(null)
                                            )
                                        }
                                    },
                                    actions = {
                                        ActionIcon(
                                            onClick = {
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateIsDeleteDialog(true)
                                                )
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateSongSelected(song)
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
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateIsUpdateDialog(true)
                                                )
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateSongSelected(song)
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
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateIsExtraDialog(true)
                                                )
                                                songListViewModel.onAction(
                                                    SongListActions.LoadPlaylists
                                                )
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateSongSelected(song)
                                                )
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
                                            if (songListState.revealedItemId != null) {
                                                songListViewModel.onAction(
                                                    SongListActions.UpdateRevealedItemId(null)
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
                onVisible = songListState.isUpdateDialog,
                onSubmit = {
                    songListViewModel.onAction(
                        SongListActions.UpdateSong
                    )
                },
                onDismiss = {
                    songListViewModel.onAction(
                        SongListActions.UpdateIsUpdateDialog(false)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateSongSelected(null)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateRevealedItemId(null)
                    )
                },
                isEnabled = songListState.songTitle.isNotBlank() && songListState.songArtist.isNotBlank(),
                title = "Update"
            ) {
                SongUpdateDialog(
                    title = songListState.songTitle,
                    onChangeTitle = { title ->
                        songListViewModel.onAction(
                            SongListActions.UpdateSongTitle(title)
                        )
                    },
                    artist = songListState.songArtist,
                    onChangeArtist = { artist ->
                        songListViewModel.onAction(
                            SongListActions.UpdateSongArtist(artist)
                        )
                    }
                )
            }

            HomeDialog(
                isMultiButton = true,
                onVisible = songListState.isDeleteDialog,
                onSubmit = {
                    songListViewModel.onAction(
                        SongListActions.DeleteSong
                    )
                },
                onDismiss = {
                    songListViewModel.onAction(
                        SongListActions.UpdateIsDeleteDialog(false)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateSongSelected(null)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateRevealedItemId(null)
                    )
                },
                title = "Delete",
                buttonBackgroundColor = Color.Red
            ) {
                SongDeleteDialog(
                    deleteTitle = songListState.songSelected?.title ?: ""
                )
            }

            HomeDialog(
                isMultiButton = false,
                onVisible = songListState.isExtraDialog,
                onSubmit = {},
                onDismiss = {
                    songListViewModel.onAction(
                        SongListActions.UpdateIsExtraDialog(false)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateSongSelected(null)
                    )
                    songListViewModel.onAction(
                        SongListActions.UpdateRevealedItemId(null)
                    )
                }
            ) {
                SongExtendDialog(
                    onPlay = {

                    },
                    onNextPlay = {

                    },
                    onDetail = {
                        songListViewModel.onAction(
                            SongListActions.UpdateIsDetailDialog(true)
                        )
                    },
                    onAddToPlaylist = {
                        songListViewModel.onAction(
                            SongListActions.UpdateIsPlaylistDialog(true)
                        )
                    },
                    onAddToWaitingList = {

                    }
                )
            }

            HomeDialog(
                isMultiButton = false,
                onVisible = songListState.isDetailDialog,
                onSubmit = {},
                onDismiss = {
                    songListViewModel.onAction(
                        SongListActions.UpdateIsDetailDialog(false)
                    )
                }
            ) {
                songListState.songSelected?.let {
                    SongDetailDialog(
                        song = it
                    )
                }
            }

            HomeDialog(
                isMultiButton = false,
                onVisible = songListState.isPlaylistDialog,
                onSubmit = {},
                onDismiss = {
                    songListViewModel.onAction(
                        SongListActions.UpdateIsPlaylistDialog(false)
                    )
                }
            ) {
                PlaylistDialog(
                    playlistList = songListState.playlist,
                    onAddToPlaylist = { playlistId ->
                        songListViewModel.addSongToPlaylist(playlistId)
                    }
                )
            }
        }
    }
}

@Composable
private fun ListSongItem(
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

@Composable
private fun UnderlinedSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = Color.White),
    onImeAction: () -> Unit = {},
    onUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onClear: () -> Unit = {},
    result: Int = 0,
    currentIndex: Int = 1
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = modifier
                .drawBehind {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 3.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onImeAction() }
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 2.dp)
                    .padding(vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(
                                    color = Color.LightGray,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (value.isNotBlank()) {
                IconButton(
                    onClick = { onClear() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        modifier = Modifier.size(25.dp),
                        contentDescription = "Clear",
                        tint = Color.White
                    )
                }
            }
        }
        if (result > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Result: $currentIndex/$result", color = Color.White)

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onDown() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            modifier = Modifier.size(60.dp),
                            contentDescription = "Down",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { onUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            modifier = Modifier.size(60.dp),
                            contentDescription = "Up",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongDeleteDialog(
    deleteTitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = deleteTitle,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Once deleted, it cannot be restored. Confirm?",
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun SongUpdateDialog(
    title: String,
    onChangeTitle: (String) -> Unit,
    artist: String,
    onChangeArtist: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Title:",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextFiled(
            value = title,
            onValueChange = { onChangeTitle(it) }
        )
        Text(
            text = "Artist:",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 15.dp)
                .padding(bottom = 8.dp)
        )
        OutlinedTextFiled(
            value = artist,
            onValueChange = { onChangeArtist(it) }
        )
    }
}

@Composable
private fun SongExtendDialog(
    onPlay: () -> Unit,
    onNextPlay: () -> Unit,
    onDetail: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onAddToWaitingList: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SongExtraButton(
            title = "Play",
            onClick = { onPlay() }
        )
        SongExtraButton(
            title = "Next play",
            onClick = { onNextPlay() }
        )
        SongExtraButton(
            title = "Detail",
            onClick = { onDetail() }
        )
        SongExtraButton(
            title = "Add to playlist",
            onClick = { onAddToPlaylist() }
        )
        SongExtraButton(
            title = "Add to waiting list",
            onClick = { onAddToWaitingList() }
        )
    }
}

@Composable
private fun SongExtraButton(
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            }
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SongDetailDialog(
    song: Song
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextDetail(
            title = "Title",
            detail = song.title
        )
        TextDetail(
            title = "Artist",
            detail = song.artist
        )
        TextDetail(
            title = "Type",
            detail = song.mimeType
        )
        TextDetail(
            title = "Format",
            detail = song.format
        )
        TextDetail(
            title = "Size",
            detail = formatSize(song.size)
        )
        TextDetail(
            title = "Duration",
            detail = formatDuration(song.duration)
        )
        TextDetail(
            title = "Date modified",
            detail = formatDateModified(song.dateModified)
        )
    }
}

@Composable
private fun TextDetail(
    title: String,
    detail: String
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$title: ")
            }
            append("\n")

            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, fontSize = 15.sp)) {
                append(detail)
            }
        },
        fontSize = 17.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun PlaylistDialog(
    playlistList: List<Playlist>,
    onAddToPlaylist: (playlistId: Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(16.dp)
    ) {
        if (playlistList.isNotEmpty()) {
            TextDetail(
                title = "All playlist",
                detail = ""
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                itemsIndexed(
                    items = playlistList,
                    key = { _, playlist -> playlist.id }
                ) { _, playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { onAddToPlaylist(playlist.id) }
                    )
                }
            }
        } else TextDetail(
            title = "Playlist",
            detail = "There are currently no playlists available."
        )
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                val color = Color.Gray
                drawLine(
                    color = color,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = playlist.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}