package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import androidx.media3.exoplayer.ExoPlayer
import com.blessingsoftware.blessingplay.core.domain.model.Song

sealed interface MusicPlayerActions {
    data class UpdateSongSelected(val song: Song?) : MusicPlayerActions

    data class UpdateIsPlaying(val isPlaying: Boolean) : MusicPlayerActions

    data class UpdateExoPlayer(val exoPlayer: ExoPlayer) : MusicPlayerActions
    data class UpdateSliderPosition(val position: Float) : MusicPlayerActions
    data class UpdateTotalDuration(val duration: Long) : MusicPlayerActions
}