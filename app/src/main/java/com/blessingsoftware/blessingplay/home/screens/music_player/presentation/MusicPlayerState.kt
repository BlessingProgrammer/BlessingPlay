package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import androidx.media3.exoplayer.ExoPlayer
import com.blessingsoftware.blessingplay.core.domain.model.Song

data class MusicPlayerState(
    val songLists: List<Song> = emptyList(),
    val songSelected : Song? = null,

    val isPlaying : Boolean = false,

    val exoPlayer : ExoPlayer? = null,
    val sliderPosition: Float = 0f,
    val totalDuration : Long = 1L
)