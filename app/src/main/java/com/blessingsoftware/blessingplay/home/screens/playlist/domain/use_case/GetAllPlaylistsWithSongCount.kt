package com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistRepository

class GetAllPlaylistsWithSongCount(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(): List<Playlist> {
        return playlistRepository.getAllPlaylistWithSongCount()
    }
}