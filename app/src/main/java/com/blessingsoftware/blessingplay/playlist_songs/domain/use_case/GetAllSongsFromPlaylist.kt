package com.blessingsoftware.blessingplay.playlist_songs.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository

class GetAllSongsFromPlaylist(
    private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
) {
    suspend operator fun invoke(playlistId: Long): List<Song> {
        return playlistSongCrossRefRepository.getSongWithPosition(playlistId = playlistId)
    }
}