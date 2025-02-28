package com.blessingsoftware.blessingplay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistEntity
import com.blessingsoftware.blessingplay.core.domain.model.Playlist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlistEntity")
    suspend fun getAllPlaylistEntities(): List<PlaylistEntity>

    @Transaction
    @Query(
        """
        SELECT p.id, p.name, p.thumbnail, COUNT(ps.songId) as songCount
        FROM PlaylistEntity p
        LEFT JOIN PlaylistSongCrossRefEntity ps ON p.id = ps.playlistId
        GROUP BY p.id
    """
    )
    suspend fun getAllPlaylistEntitiesWithSongCount(): List<Playlist>

    @Upsert
    suspend fun upsertPlaylistEntity(playlistEntity: PlaylistEntity)

    @Update
    suspend fun updatePlaylistEntity(playlistEntity: PlaylistEntity)

    @Transaction
    suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity) {
        playlistEntity.id?.let { deletePlaylistSongs(playlistId = it) }
        playlistEntity.id?.let { deletePlaylistEntityById(id = it) }
    }

    @Query("DELETE FROM PlaylistSongCrossRefEntity WHERE playlistId = :playlistId")
    suspend fun deletePlaylistSongs(playlistId: Long)

    @Query("DELETE FROM PlaylistEntity WHERE id = :id")
    suspend fun deletePlaylistEntityById(id: Long)
}