package com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository

class InsertSongs(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(){
        songRepository.insertSongs()
    }
}