package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.formatDuration

@Composable
fun MusicPlayerScreen(
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
) {
    val musicPlayerState by musicPlayerViewModel.musicPlayerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        MusicProfile(
            song = musicPlayerState.songSelected,
            isPlaying = musicPlayerState.isPlaying
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.padding(bottom = 15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = formatDuration(musicPlayerState.currentDuration.toLong()))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = formatDuration(musicPlayerState.maxDuration.toLong()))
                }
                key(musicPlayerState.maxDuration) {
                    CustomSlider(
                        value = musicPlayerState.currentDuration,
                        onValueChange = { newValue ->
                            musicPlayerViewModel.updateCurrentPosition(newValue)
                        },
                        onValueChangeFinished = { finalValue ->
                            musicPlayerViewModel.onSliderValueChanged(finalValue)
                        },
                        valueRange = 0f..musicPlayerState.maxDuration,
                        modifier = Modifier.fillMaxWidth(),
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.DarkGray,
                        thumbColor = Color.White
                    )
                }
            }

            MusicController(
                isPlay = musicPlayerState.isPlaying,
                shuffleOnClick = {
                    musicPlayerViewModel.setShuffleStatus(it)
                },
                shuffleStatus = musicPlayerState.currentShuffleStatus,
                skipPreviousOnClick = {
                    musicPlayerViewModel.prevSong()
                },
                controllerOnClick = {
                    musicPlayerViewModel.playPauseMusic()
                },
                skipNextOnClick = {
                    musicPlayerViewModel.nextSong()
                },
                repeatModeOption = musicPlayerState.currentRepeatModeOption,
                repeatModeOnClick = {
                    musicPlayerViewModel.setRepeatModeOption(it)
                },
                isEnable = musicPlayerState.songSelected != null
            )
        }
    }
}

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    trackHeight: Float = 12f,
    thumbRadius: Float = 25f,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    thumbColor: Color,
) {
    var thumbPosition by remember { mutableFloatStateOf(value) }
    var isDragging by remember { mutableStateOf(false) }
    var canvasWidth by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(value) {
        if (!isDragging) {
            thumbPosition = value
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(15.dp)
            .onGloballyPositioned { coordinates ->
                canvasWidth = coordinates.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val clampedX = offset.x.coerceIn(0f, canvasWidth)
                        val newPosition =
                            (clampedX / canvasWidth) * (valueRange.endInclusive - valueRange.start) + valueRange.start
                        thumbPosition =
                            newPosition.coerceIn(valueRange.start, valueRange.endInclusive)
                        onValueChange(thumbPosition)
                    },
                    onDrag = { change, _ ->
                        val clampedX = change.position.x.coerceIn(0f, canvasWidth)
                        val newPosition =
                            (clampedX / canvasWidth) * (valueRange.endInclusive - valueRange.start) + valueRange.start
                        thumbPosition =
                            newPosition.coerceIn(valueRange.start, valueRange.endInclusive)
                        onValueChange(thumbPosition)
                    },
                    onDragEnd = {
                        isDragging = false
                        onValueChangeFinished(thumbPosition)
                    }
                )
            }
    ) {
        val effectiveWidth = if (canvasWidth > 0) canvasWidth else size.width
        val trackStart = Offset(0f, center.y)
        val trackEnd = Offset(effectiveWidth, center.y)
        val currentValue = if (isDragging) thumbPosition else value
        val thumbX =
            ((currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * effectiveWidth
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
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    var lastRotation by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying, rotationAngle) {
        if (isPlaying) {
            lastRotation = rotationAngle
        }
    }

    val finalRotation = if (isPlaying) rotationAngle else lastRotation

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.Transparent)
        ) {

            if (!song?.albumArt.isNullOrEmpty()) {
                AsyncImage(
                    model = song?.albumArt,
                    contentDescription = "${song?.artistId} Bg",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.FillHeight,
                    alpha = 0.35f
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.album_art_default),
                    contentDescription = "Album Art Default Bg",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.FillHeight,
                    alpha = 0.3f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.Transparent)
                    .padding(50.dp)
                    .rotate(finalRotation)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vinyl_bg),
                    contentDescription = "Vinyl Art",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )

                if (!song?.albumArt.isNullOrEmpty()) {
                    AsyncImage(
                        model = song?.albumArt,
                        contentDescription = "${song?.artistId} Thumbnail",
                        modifier = Modifier
                            .size(105.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.album_art_default),
                        contentDescription = "Album Art Default",
                        modifier = Modifier
                            .size(105.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = song?.title ?: "Loading...",
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
            text = song?.artist ?: "Loading...",
            fontSize = 15.sp,
            fontWeight = FontWeight.Thin,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
private fun MusicController(
    isEnable: Boolean,
    isPlay: Boolean = false,
    shuffleOnClick: (status: Boolean) -> Unit,
    shuffleStatus: Boolean,
    skipPreviousOnClick: () -> Unit,
    controllerOnClick: () -> Unit,
    skipNextOnClick: () -> Unit,
    repeatModeOnClick: (option: Boolean) -> Unit,
    repeatModeOption: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        MusicControllerButton(
            onClick = {
                when (shuffleStatus) {
                    true -> shuffleOnClick(false)
                    false -> shuffleOnClick(true)
                }
            },
            icon = Icons.Default.Shuffle,
            modifier = Modifier
                .width(30.dp),
            contentDescription = "Shuffle",
            tint = when (shuffleStatus) {
                true -> Color.White
                false -> Color.DarkGray
            },
            isEnable = isEnable
        )
        Row(
            modifier = Modifier.padding(horizontal = 35.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MusicControllerButton(
                onClick = { skipPreviousOnClick() },
                icon = Icons.Default.SkipPrevious,
                modifier = Modifier
                    .size(40.dp),
                contentDescription = "SkipPrevious",
                tint = if (isEnable) Color.White else Color.DarkGray,
                isEnable = isEnable
            )
            Spacer(modifier = Modifier.width(15.dp))
            MusicControllerButton(
                onClick = { controllerOnClick() },
                icon = if (isPlay) Icons.Outlined.PauseCircle else Icons.Outlined.PlayCircle,
                modifier = Modifier.size(80.dp),
                contentDescription = "ControllerCircle",
                tint = if (isEnable) Color.White else Color.DarkGray,
                isEnable = isEnable
            )
            Spacer(modifier = Modifier.width(15.dp))
            MusicControllerButton(
                onClick = { skipNextOnClick() },
                icon = Icons.Default.SkipNext,
                modifier = Modifier
                    .size(40.dp),
                contentDescription = "SkipNext",
                tint = if (isEnable) Color.White else Color.DarkGray,
                isEnable = isEnable
            )
        }
        MusicControllerButton(
            onClick = {
                when (repeatModeOption) {
                    true -> repeatModeOnClick(false)
                    false -> repeatModeOnClick(true)
                }
            },
            icon = when (repeatModeOption) {
                true -> Icons.Default.RepeatOne
                false -> Icons.Default.Repeat
            },
            modifier = Modifier
                .width(30.dp),
            contentDescription = "Repeat",
            tint = if (isEnable) Color.White else Color.DarkGray,
            isEnable = isEnable
        )
    }
}

@Composable
private fun MusicControllerButton(
    onClick: () -> Unit,
    isEnable: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color,
    contentDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        enabled = isEnable,
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