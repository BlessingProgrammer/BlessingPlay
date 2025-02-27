package com.blessingsoftware.blessingplay.core.data.repository

import com.blessingsoftware.blessingplay.core.data.local.AppDb
import com.blessingsoftware.blessingplay.core.data.mapper.toPlaylist
import com.blessingsoftware.blessingplay.core.data.mapper.toPlaylistEntityForInsert
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    appDb: AppDb
) : PlaylistRepository {
    private val playlistDao = appDb.playlistDao

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylistEntities().map { it.toPlaylist() }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        return playlistDao.upsertPlaylistEntity(playlist.toPlaylistEntityForInsert())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistDao.updatePlaylistEntity(playlist.toPlaylistEntityForInsert())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return playlistDao.deletePlaylistEntity(playlist.toPlaylistEntityForInsert())
    }

}