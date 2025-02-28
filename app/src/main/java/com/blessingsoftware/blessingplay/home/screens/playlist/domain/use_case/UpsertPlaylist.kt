package com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistRepository

class UpsertPlaylist(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(
        name: String,
        thumbnail: String
    ): Boolean {
        if (name.isBlank()) {
            return false
        }
        val playlist = Playlist(
            name = name,
            thumbnail = thumbnail
        )
        playlistRepository.upsertPlaylist(playlist)
        return true
    }
}