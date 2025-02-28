package com.blessingsoftware.blessingplay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntity

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

    @Transaction
    suspend fun deleteSongEntity(songEntity: SongEntity) {
        songEntity.id?.let {
            deleteSongPlaylists(songId = it)
            deleteSongEntityById(id = it)
            shiftPositionsAfterSongEntityDeleted(songId = it)
        }
    }

    @Query("DELETE FROM PlaylistSongCrossRefEntity WHERE songId = :songId")
    suspend fun deleteSongPlaylists(songId: Long)

    @Query("DELETE FROM SongEntity WHERE id = :id")
    suspend fun deleteSongEntityById(id: Long)

    @Query(
        """
        UPDATE PlaylistSongCrossRefEntity
        SET position = position - 1
        WHERE playlistId IN (
            SELECT playlistId FROM PlaylistSongCrossRefEntity WHERE songId = :songId
        ) AND position > (
            SELECT position FROM PlaylistSongCrossRefEntity WHERE songId = :songId
    )
    """
    )
    suspend fun shiftPositionsAfterSongEntityDeleted(songId: Long)
}