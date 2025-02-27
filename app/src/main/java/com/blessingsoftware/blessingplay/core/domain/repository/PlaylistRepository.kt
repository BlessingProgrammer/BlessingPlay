package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.domain.model.Playlist

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<Playlist>

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}