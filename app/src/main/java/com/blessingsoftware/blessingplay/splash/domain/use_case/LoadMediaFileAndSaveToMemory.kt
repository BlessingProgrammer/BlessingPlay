package com.blessingsoftware.blessingplay.splash.domain.use_case

import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository
import javax.inject.Inject

class LoadMediaFileAndSaveToMemory @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke() {
        songRepository.loadMediaFileAndSaveToMemory()
    }
}