package com.blessingsoftware.blessingplay.playlist_songs.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository

class UpdateSongPosition(
    private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
) {
    suspend operator fun invoke(
        playlistId: Long,
        songId: Long,
        oldPosition: Long,
        newPosition: Long
    ) {
        playlistSongCrossRefRepository.updateSongPosition(
            playlistId = playlistId,
            songId = songId,
            oldPosition = oldPosition,
            newPosition = newPosition
        )
    }
}