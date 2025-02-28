package com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository

class AddSongToPlaylist(
    private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
) {
    suspend operator fun invoke(playlistId: Long, songId: Long): Boolean {
        val result = playlistSongCrossRefRepository.addSongToPlaylist(
            playlistId = playlistId,
            songId = songId
        )
        return result
    }
}