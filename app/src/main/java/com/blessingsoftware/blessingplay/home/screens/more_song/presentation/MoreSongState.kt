package com.blessingsoftware.blessingplay.home.screens.more_song.presentation

import com.blessingsoftware.blessingplay.core.domain.model.MoreSong

data class MoreSongState(
    val url: String? = null,
    val isLoading: Boolean = false,

    val moreSong: MoreSong? = null,
    val status: String? = null,
    val message: String? = null,

    val progress: Int = 0
)
