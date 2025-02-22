package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.domain.model.Song

interface SongRepository {
    suspend fun loadMediaFileAndSaveToDb()

    suspend fun getAllSongs(): List<Song>

    suspend fun insertSongs()

    suspend fun updateSong(song: Song)

    suspend fun deleteSong(song: Song)
}
