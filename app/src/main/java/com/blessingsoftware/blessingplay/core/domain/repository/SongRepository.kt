package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.domain.model.Song

interface SongRepository {
    suspend fun loadMediaFileAndSaveToMemory()

    suspend fun getAllSongs(): List<Song>

    suspend fun upsertSong(song: Song)

    suspend fun updateSong(song: Song)

    suspend fun deleteSong(song: Song)
}
