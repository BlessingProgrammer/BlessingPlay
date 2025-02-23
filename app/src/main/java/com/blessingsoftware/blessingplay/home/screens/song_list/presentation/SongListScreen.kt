package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val revealedItemId by songListViewModel.revealedItemId.collectAsState()
    val isLoading by songListViewModel.isLoading.collectAsState()
    val searchQuery by songListViewModel.searchQuery.collectAsState()
    val queryIndexes = songListViewModel.calculateScrollTargetIndex()
    val updateIndex by songListViewModel.updateIndex.collectAsState()

    val currentQueryIndex = remember { mutableIntStateOf(-1) }

    val listState = rememberLazyListState()
    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    val songStateForUpdate by songListViewModel.songStateForUpdate.collectAsState()
    val updateTitle = remember { mutableStateOf("") }
    val updateArtist = remember { mutableStateOf("") }

    val songStateForDelete by songListViewModel.songStateForDelete.collectAsState()

    val songStateForDetail by songListViewModel.songStateForDetail.collectAsState()

    val extendDialogVisible = remember { mutableStateOf(false) }

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
            Column {
                Text(
                    text = "Song list",
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
                UnderlinedSearchTextField(
                    value = searchQuery,
                    onValueChange = { songListViewModel.setSearchQuery(it) },
                    placeholder = "Search songs...",
                    modifier = Modifier.fillMaxWidth(),
                    onImeAction = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index = queryIndexes.first()
                            )
                            currentQueryIndex.value = 0
                        }
                    },
                    onUp = {
                        coroutineScope.launch {
                            if (queryIndexes.isNotEmpty()) {
                                val prevIndex = if (currentQueryIndex.value > 0) {
                                    currentQueryIndex.value - 1
                                } else {
                                    queryIndexes.size - 1
                                }

                                currentQueryIndex.value = prevIndex

                                listState.animateScrollToItem(
                                    index = queryIndexes[prevIndex]
                                )
                            }
                        }
                    },
                    onDown = {
                        coroutineScope.launch {
                            if (queryIndexes.isNotEmpty()) {
                                val nextIndex =
                                    if (currentQueryIndex.value < queryIndexes.size - 1) {
                                        currentQueryIndex.value + 1
                                    } else {
                                        0
                                    }

                                currentQueryIndex.value = nextIndex

                                listState.animateScrollToItem(
                                    index = queryIndexes[nextIndex],
                                    scrollOffset = 0
                                )
                            }
                        }
                    },
                    onClear = {
                        songListViewModel.setSearchQuery("")
                        currentQueryIndex.value = -1
                    },
                    result = queryIndexes.size,
                    currentIndex = currentQueryIndex.value + 1
                )
            }
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
                            isRevealed = revealedItemId == song.id,
                            onExpanded = { songListViewModel.setRevealedItemId(song.id) },
                            onCollapsed = {
                                if (revealedItemId == song.id) {
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
                                        extendDialogVisible.value = true
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
                isMultiButton = true,
                onVisible = songStateForUpdate != null,
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
                isEnabled = updateTitle.value.isNotBlank() && updateArtist.value.isNotBlank(),
                title = "Update"
            ) {
                SongUpdateDialog(
                    updateTitle = updateTitle,
                    updateArtist = updateArtist
                )
            }

            HomeDialog(
                isMultiButton = true,
                onVisible = songStateForDelete != null,
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
                buttonBackgroundColor = Color.Red
            ) {
                SongDeleteDialog(
                    deleteTitle = songStateForDelete?.title ?: ""
                )
            }

            HomeDialog(
                isMultiButton = false,
                onVisible = extendDialogVisible.value,
                onSubmit = {},
                onDismiss = {
                    extendDialogVisible.value = false
                    songListViewModel.setRevealedItemId(null)
                }
            ) {
                SongExtendDialog(
                    onPlay = {

                    },
                    onNextPlay = {

                    },
                    onDetail = {
                        songListViewModel.setSongStateForDetail(revealedItemId)
                    },
                    onAddToPlaylist = {

                    },
                    onAddToWaitingList = {

                    }
                )
            }

            HomeDialog(
                isMultiButton = false,
                onVisible = songStateForDetail != null,
                onSubmit = {},
                onDismiss = {
                    songListViewModel.setSongStateForDetail(null)
                }
            ) {
                songStateForDetail?.let {
                    SongDetailDialog(
                        song = it
                    )
                }
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
    updateTitle: MutableState<String>,
    updateArtist: MutableState<String>,
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
