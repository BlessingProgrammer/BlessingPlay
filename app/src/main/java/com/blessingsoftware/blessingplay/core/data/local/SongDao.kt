package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {
    @Query("SELECT * FROM songEntity")
    suspend fun getAllSongEntities(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun firstInsertSongEntities(songEntities: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongEntities(songEntities: List<SongEntity>)

    @Update
    suspend fun updateSongEntity(songEntity: SongEntity)

    @Delete
    suspend fun deleteSongEntity(songEntity: SongEntity)
}