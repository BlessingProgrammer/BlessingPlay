package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface SongDao {
    @Query("SELECT * FROM songEntity")
    suspend fun getAllSongs(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSongs(songEntities: List<SongEntity>)

    @Upsert
    suspend fun upsertSong(songEntity: SongEntity)

    @Update
    suspend fun updateSong(songEntity: SongEntity)

    @Delete
    suspend fun deleteSong(songEntity: SongEntity)
}