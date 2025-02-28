package com.blessingsoftware.blessingplay.playlist_songs.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository

class DeleteSongFromPlaylist(
    private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
) {
    suspend operator fun invoke(playlistId: Long, songId: Long, position: Long) {
        playlistSongCrossRefRepository.deleteSongFromPlaylist(
            playlistId = playlistId,
            songId = songId,
            position = position
        )
    }
}