package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntityWithPosition
import com.blessingsoftware.blessingplay.core.domain.model.Song

fun SongEntityWithPosition.toSong(): Song {
    return Song(
        fileId = songEntity.fileId,
        title = songEntity.title,
        fileName = songEntity.fileName,
        path = songEntity.path,
        albumArt = songEntity.albumArt,
        artistId = songEntity.artistId,
        artist = songEntity.artist,
        format = songEntity.format,
        mimeType = songEntity.mimeType,
        size = songEntity.size,
        duration = songEntity.duration,
        dateModified = songEntity.dateModified,
        position = position,
        id = requireNotNull(songEntity.id)
    )
}