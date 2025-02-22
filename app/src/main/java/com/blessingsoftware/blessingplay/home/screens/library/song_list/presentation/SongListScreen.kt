package com.blessingsoftware.blessingplay.home.screens.library.song_list.presentation

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Song
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
    val revealedItemId by songListViewModel.revealedItemId.collectAsState()
    val isLoading by songListViewModel.isLoading.collectAsState()
    val searchQuery by songListViewModel.searchQuery.collectAsState()
    val queryIndex = songListViewModel.calculateScrollTargetIndex()
    val updateIndex by songListViewModel.updateIndex.collectAsState()

    val listState = rememberLazyListState()
    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    val songStateForUpdate by songListViewModel.songStateForUpdate.collectAsState()
    val updateTitle = remember { mutableStateOf("") }
    val updateArtist = remember { mutableStateOf("") }

    val songStateForDelete by songListViewModel.songStateForDelete.collectAsState()

    LaunchedEffect(songStateForUpdate) {
        updateTitle.value = songStateForUpdate?.title ?: ""
        updateArtist.value = songStateForUpdate?.artist ?: ""
    }

    LaunchedEffect(updateIndex) {
        updateIndex?.let { listState.animateScrollToItem(index = it, scrollOffset = 0) }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            if (listState.firstVisibleItemIndex != 0) {
                listState.scrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            UnderlinedSearchTextField(
                value = searchQuery,
                onValueChange = { songListViewModel.setSearchQuery(it) },
                placeholder = "Search songs...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onImeAction = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = queryIndex, scrollOffset = 0)
                    }
                },
                onClear = { songListViewModel.setSearchQuery("") }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isLoading,
            onRefresh = {
                songListViewModel.pullToRefresh()
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                songListState.forEach { (header, songs) ->
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
                                        songListViewModel.setSongStateForDelete(song)
                                    },
                                    backgroundColor = Color.Red,
                                    icon = Icons.Default.Delete,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(60.dp)
                                )
                                ActionIcon(
                                    onClick = {
                                        songListViewModel.setSongStateForUpdate(song)
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
                                    if (revealedItemId != null) {
                                        songListViewModel.setRevealedItemId(null)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            HomeDialog(
                onVisible = songStateForUpdate !== null,
                onSubmit = {
                    songStateForUpdate?.let {
                        songListViewModel.updateSong(
                            song = it,
                            updateTitle = updateTitle.value,
                            updateArtist = updateArtist.value
                        )
                    }
                },
                onDismiss = {
                    songListViewModel.setSongStateForUpdate(null)
                    songListViewModel.setRevealedItemId(null)
                },
                title = "Update",
                buttonBackgroundColor = Color.Blue,
                buttonTextColor = Color.White
            ) {
                SongUpdate(
                    updateTitle = updateTitle,
                    updateArtist = updateArtist
                )
            }

            HomeDialog(
                onVisible = songStateForDelete !== null,
                onSubmit = {
                    songStateForDelete?.let {
                        songListViewModel.deleteSong(
                            song = it
                        )
                    }
                },
                onDismiss = {
                    songListViewModel.setSongStateForDelete(null)
                    songListViewModel.setRevealedItemId(null)
                },
                title = "Delete",
                buttonBackgroundColor = Color.Red,
                buttonTextColor = Color.White
            ) {
                SongDelete(
                    deleteTitle = songStateForDelete?.title ?: ""
                )
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

@Composable
fun UnderlinedSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = Color.White),
    onImeAction: () -> Unit = {},
    onClear: () -> Unit = {}
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
                .padding(bottom = 8.dp),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle.copy(
                                color = Color.LightGray,
                                fontStyle = FontStyle.Italic
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (value.isNotBlank()) IconButton(
            onClick = { onClear() },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SongUpdate(
    updateTitle: MutableState<String>,
    updateArtist: MutableState<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Title:",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextFiled(
            value = updateTitle.value,
            onValueChange = { updateTitle.value = it }
        )
        Text(
            text = "Artist:",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 15.dp)
                .padding(bottom = 8.dp)
        )
        OutlinedTextFiled(
            value = updateArtist.value,
            onValueChange = { updateArtist.value = it }
        )
    }
}

@Composable
fun SongDelete(
    deleteTitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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