package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlistEntity")
    suspend fun getAllPlaylistEntities(): List<PlaylistEntity>

    @Upsert
    suspend fun upsertPlaylistEntity(playlistEntity: PlaylistEntity)

    @Update
    suspend fun updatePlaylistEntity(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity)
}