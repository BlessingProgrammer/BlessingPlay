package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SongRemovedDao {
    @Query("SELECT * FROM songRemovedEntity")
    suspend fun getAllSongRemovedEntities(): List<SongRemovedEntity>

    @Upsert
    suspend fun upsertSongRemovedEntity(songRemovedEntity: SongRemovedEntity)
}