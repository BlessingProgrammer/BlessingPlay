package com.blessingsoftware.blessingplay.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistSongCrossRefEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntityWithPosition

@Dao
interface PlaylistSongCrossRefDao {
    @Transaction
    @Query(
        """
        SELECT s.*, ps.position FROM SongEntity s
        INNER JOIN PlaylistSongCrossRefEntity ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.position ASC
    """
    )
    suspend fun getSongEntitiesWithPositionInPlaylist(
        playlistId: Long
    ): List<SongEntityWithPosition>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongCrossRefEntity(
        playlistSongCrossRefEntity: PlaylistSongCrossRefEntity
    ): Long

    @Query(
        """
        UPDATE PlaylistSongCrossRefEntity 
        SET position = :newPosition 
        WHERE playlistId = :playlistId AND songId = :songId
    """
    )
    suspend fun updateSongPosition(playlistId: Long, songId: Long, newPosition: Long)

    @Query(
        """
    UPDATE PlaylistSongCrossRefEntity
    SET position = position - 1
    WHERE playlistId = :playlistId
      AND songId != :songId
      AND position BETWEEN :oldPosition + 1 AND :newPosition
    """
    )
    suspend fun decrementPositionsInRange(playlistId: Long, songId: Long, oldPosition: Long, newPosition: Long)

    @Query(
        """
    UPDATE PlaylistSongCrossRefEntity
    SET position = position + 1
    WHERE playlistId = :playlistId
      AND songId != :songId
      AND position BETWEEN :newPosition AND :oldPosition - 1
    """
    )
    suspend fun incrementPositionsInRange(playlistId: Long, songId: Long, oldPosition: Long, newPosition: Long)

    @Transaction
    suspend fun updateSongPositionAndAdjustOthers(
        playlistId: Long,
        songId: Long,
        oldPosition: Long,
        newPosition: Long
    ) {
        if (newPosition > oldPosition) {
            decrementPositionsInRange(playlistId, songId, oldPosition, newPosition)
        } else if (newPosition < oldPosition) {
            incrementPositionsInRange(playlistId, songId, oldPosition, newPosition)
        }
        updateSongPosition(playlistId, songId, newPosition)
    }

    @Query(
        """
        SELECT COALESCE(MAX(position), 0) + 1 
        FROM PlaylistSongCrossRefEntity 
        WHERE playlistId = :playlistId
    """
    )
    suspend fun getNextSongPosition(playlistId: Long): Long

    @Query(
        """
        DELETE FROM PlaylistSongCrossRefEntity 
        WHERE playlistId = :playlistId AND songId = :songId
    """
    )
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query(
        """
        UPDATE PlaylistSongCrossRefEntity 
        SET position = position - 1 
        WHERE playlistId = :playlistId AND position > :removedPosition
    """
    )
    suspend fun shiftPositionsAfterRemovalSongFromPlaylist(
        playlistId: Long,
        removedPosition: Long
    )
}