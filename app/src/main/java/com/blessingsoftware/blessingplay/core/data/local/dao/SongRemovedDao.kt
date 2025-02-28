package com.blessingsoftware.blessingplay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.blessingsoftware.blessingplay.core.data.local.entities.SongRemovedEntity

@Dao
interface SongRemovedDao {
    @Query("SELECT * FROM songRemovedEntity")
    suspend fun getAllSongRemovedEntities(): List<SongRemovedEntity>

    @Upsert
    suspend fun upsertSongRemovedEntity(songRemovedEntity: SongRemovedEntity)
}