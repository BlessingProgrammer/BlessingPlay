package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntityWithPosition
import com.blessingsoftware.blessingplay.core.domain.model.Song

fun listSongEntityWithPositionToListSong(list: List<SongEntityWithPosition>): List<Song> {
    return list.map { it.toSong() }
}

fun listSongEntityToListSong(list: List<SongEntity>): List<Song> {
    return list.map { it.toSong() }
}