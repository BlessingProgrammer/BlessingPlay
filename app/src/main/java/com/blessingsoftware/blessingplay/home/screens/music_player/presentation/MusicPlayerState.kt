package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Song

data class MusicPlayerState(
    val songSelected: Song? = null,
    val isLoading: Boolean = false,

    val isPlaying: Boolean = false,

    val maxDuration: Float = 0f,
    val currentDuration: Float = 0f,

    val currentRepeatModeOption: Boolean = false,
    val currentShuffleStatus: Boolean = false
)