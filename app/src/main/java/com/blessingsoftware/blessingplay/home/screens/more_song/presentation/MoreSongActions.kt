package com.blessingsoftware.blessingplay.home.screens.more_song.presentation

import com.blessingsoftware.blessingplay.core.domain.model.MoreSong

sealed interface MoreSongActions {
    data class UpdateUrl(val url: String) : MoreSongActions
    data class UpdateIsLoading(val isLoading: Boolean) : MoreSongActions

    data class UpdateMoreSong(val moreSong: MoreSong?) : MoreSongActions

    data class UpdateCode(val code: Int?) : MoreSongActions
    data class UpdateMessage(val message: String?) : MoreSongActions

    data class UpdateProgress(val progress: Int) : MoreSongActions
}