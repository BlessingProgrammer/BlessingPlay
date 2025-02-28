package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.domain.model.Playlist

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<Playlist>

    suspend fun getAllPlaylistWithSongCount(): List<Playlist>

    suspend fun upsertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}