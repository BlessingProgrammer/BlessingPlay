package com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository

class UpdateSong(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(song: Song) {
        songRepository.updateSong(song)
    }
}