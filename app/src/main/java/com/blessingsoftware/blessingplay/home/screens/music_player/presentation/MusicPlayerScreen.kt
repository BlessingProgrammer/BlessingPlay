package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.formatDuration
import kotlinx.coroutines.delay

@Composable
fun MusicPlayerScreen(
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
) {
    val musicPlayerState by musicPlayerViewModel.musicPlayerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) {
        musicPlayerViewModel.onAction(
            MusicPlayerActions.UpdateExoPlayer(
                ExoPlayer.Builder(context).build()
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            musicPlayerState.exoPlayer?.let {
                it.release()
            }
        }
    }

    LaunchedEffect(musicPlayerState.songSelected) {
        musicPlayerState.songSelected?.let {
            val mediaItem = MediaItem.fromUri(Uri.parse(it.path))
            musicPlayerState.exoPlayer?.let {
                it.setMediaItem(mediaItem)
                it.prepare()
            }
        }
    }

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val totalDuration = musicPlayerState.songSelected?.duration ?: 1L

    LaunchedEffect(musicPlayerState.exoPlayer) {
        while (true) {
            musicPlayerState.exoPlayer?.let {
                sliderPosition = it.currentPosition.toFloat()
            }
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        MusicProfile(song = musicPlayerState.songSelected, isPlaying = musicPlayerState.isPlaying)

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formatDuration(sliderPosition.toLong()))
                Spacer(modifier = Modifier.weight(1f))
                musicPlayerState.songSelected?.let {
                    Text(text = formatDuration(it.duration))
                }
            }

            CustomSlider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    sliderPosition = newValue
                },
                onValueChangeFinished = {
                    musicPlayerState.exoPlayer?.let {
                        it.seekTo(sliderPosition.toLong())
                    }
                },
                valueRange = 0f..totalDuration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.DarkGray,
                thumbColor = Color.White
            )
        }

        MusicController(
            isPlay = musicPlayerState.isPlaying,
            skipPreviousOnClick = {},
            controllerOnClick = {
                if (musicPlayerState.isPlaying) {
                    musicPlayerState.exoPlayer?.let {
                        it.pause()
                    }
                } else {
                    musicPlayerState.exoPlayer?.let {
                        it.play()
                    }
                }
                musicPlayerViewModel.onAction(
                    MusicPlayerActions.UpdateIsPlaying(!musicPlayerState.isPlaying)
                )
            },
            skipNextOnClick = {}
        )
    }
}

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    trackHeight: Float = 12f,
    thumbRadius: Float = 30f,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    thumbColor: Color,
) {
    var thumbPosition by remember { mutableFloatStateOf(value) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (!isDragging) {
            thumbPosition = value
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(15.dp)
            .padding(horizontal = 3.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val newPosition =
                            (offset.x / size.width) * (valueRange.endInclusive - valueRange.start) + valueRange.start
                        thumbPosition = newPosition.coerceIn(valueRange)
                        onValueChange(thumbPosition)
                    },
                    onDrag = { change, _ ->
                        val newPosition =
                            (change.position.x / size.width) * (valueRange.endInclusive - valueRange.start) + valueRange.start
                        thumbPosition = newPosition.coerceIn(valueRange)
                        onValueChange(thumbPosition)
                    },
                    onDragEnd = {
                        isDragging = false
                        onValueChangeFinished()
                    }
                )
            }
    ) {
        val trackStart = Offset(0f, center.y)
        val trackEnd = Offset(size.width, center.y)
        val currentValue = if (isDragging) thumbPosition else value
        val thumbX =
            ((currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * size.width
        val thumbCenter = Offset(thumbX, center.y)

        drawLine(
            color = inactiveTrackColor,
            start = trackStart,
            end = trackEnd,
            strokeWidth = trackHeight
        )

        drawLine(
            color = activeTrackColor,
            start = trackStart,
            end = Offset(thumbX, center.y),
            strokeWidth = trackHeight
        )

        drawCircle(
            color = thumbColor,
            radius = thumbRadius,
            center = thumbCenter
        )
    }
}

@Composable
private fun MusicProfile(song: Song?, isPlaying: Boolean) {

    val rotationAngle by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val finalRotation = if (isPlaying) rotationAngle else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        song?.let {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(5.dp)
                        .rotate(finalRotation)
                ) {
                    if (!song.albumArt.isNullOrEmpty()) {
                        AsyncImage(
                            model = song.albumArt,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.music),
                            contentDescription = "Default Album Art",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(width = 5.dp, shape = CircleShape, color = Color.White)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = song.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isPlaying) Modifier.basicMarquee() else Modifier)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = song.artist,
                fontSize = 14.sp,
                fontWeight = FontWeight.Thin,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có bài hát nào được chọn",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun MusicController(
    isPlay: Boolean = false,
    skipPreviousOnClick: () -> Unit,
    controllerOnClick: () -> Unit,
    skipNextOnClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        MusicControllerButton(
            onClick = { skipPreviousOnClick() },
            icon = Icons.Default.SkipPrevious,
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp),
            contentDescription = "SkipPrevious",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(20.dp))
        MusicControllerButton(
            onClick = { controllerOnClick() },
            icon = if (isPlay) Icons.Outlined.PauseCircle else Icons.Outlined.PlayCircle,
            modifier = Modifier
                .size(75.dp),
            contentDescription = "ControllerCircle",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(20.dp))
        MusicControllerButton(
            onClick = { skipNextOnClick() },
            icon = Icons.Default.SkipNext,
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp),
            contentDescription = "SkipNext",
            tint = Color.White
        )
    }
}

@Composable
private fun MusicControllerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color,
    contentDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(Color.Transparent)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}