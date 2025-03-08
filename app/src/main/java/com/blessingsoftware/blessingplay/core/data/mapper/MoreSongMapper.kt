package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.remote.dto.MoreSongDto
import com.blessingsoftware.blessingplay.core.domain.model.MoreSong

fun MoreSongDto.toMoreSong() : MoreSong{
    return MoreSong(
        songPath = songPath
    )
}