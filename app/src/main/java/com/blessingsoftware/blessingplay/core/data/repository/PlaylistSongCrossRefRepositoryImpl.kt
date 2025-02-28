package com.blessingsoftware.blessingplay.core.data.repository

import com.blessingsoftware.blessingplay.core.data.local.AppDb
import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistSongCrossRefEntity
import com.blessingsoftware.blessingplay.core.data.mapper.toSong
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository

class PlaylistSongCrossRefRepositoryImpl(
    appDb: AppDb
) : PlaylistSongCrossRefRepository {
    private val playlistSongCrossRefDao = appDb.playlistSongCrossRefDao

    override suspend fun getSongWithPosition(playlistId: Long): List<Song> {
        return playlistSongCrossRefDao.getSongEntitiesWithPositionInPlaylist(playlistId)
            .map { it.toSong() }
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long): Boolean {
        val position = playlistSongCrossRefDao.getNextSongPosition(playlistId)
        val result = playlistSongCrossRefDao.insertPlaylistSongCrossRefEntity(
            PlaylistSongCrossRefEntity(
                playlistId = playlistId,
                songId = songId,
                position = position
            )
        )
        return result != -1L
    }

    override suspend fun updateSongPosition(
        playlistId: Long,
        songId: Long,
        oldPosition: Long,
        newPosition: Long
    ) {
        playlistSongCrossRefDao.updateSongPositionAndAdjustOthers(
            playlistId = playlistId,
            songId = songId,
            oldPosition = oldPosition,
            newPosition = newPosition
        )
    }

    override suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long, position: Long) {
        playlistSongCrossRefDao.removeSongFromPlaylist(playlistId = playlistId, songId = songId)
        playlistSongCrossRefDao.shiftPositionsAfterRemovalSongFromPlaylist(
            playlistId = playlistId,
            removedPosition = position
        )
    }
}