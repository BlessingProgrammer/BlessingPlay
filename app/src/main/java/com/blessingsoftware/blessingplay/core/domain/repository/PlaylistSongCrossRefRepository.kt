package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.domain.model.Song

interface PlaylistSongCrossRefRepository {
    suspend fun getSongWithPosition(playlistId: Long): List<Song>

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long): Boolean

    suspend fun updateSongPosition(
        playlistId: Long,
        songId: Long,
        oldPosition: Long,
        newPosition: Long
    )

    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long, position: Long)
}