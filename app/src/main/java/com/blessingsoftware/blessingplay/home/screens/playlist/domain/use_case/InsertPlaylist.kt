package com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistRepository

class InsertPlaylist(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }
}