package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.SongRemovedEntity
import com.blessingsoftware.blessingplay.core.domain.model.Song

fun Song.toSongEntityForUpdate(): SongEntity {
    return SongEntity(
        fileId = fileId,
        title = title,
        fileName = fileName,
        path = path,
        albumArt = albumArt,
        artistId = artistId,
        artist = artist,
        format = format,
        mimeType = mimeType,
        size = size,
        duration = duration,
        dateModified = dateModified,
        id = id
    )
}

fun Song.toSongEntityForDelete(): SongEntity {
    return SongEntity(
        fileId = fileId,
        title = title,
        fileName = fileName,
        path = path,
        albumArt = albumArt,
        artistId = artistId,
        artist = artist,
        format = format,
        mimeType = mimeType,
        size = size,
        duration = duration,
        dateModified = dateModified,
        id = id
    )
}

fun SongEntity.toSong(): Song {
    return Song(
        fileId = fileId,
        title = title,
        fileName = fileName,
        path = path,
        albumArt = albumArt,
        artistId = artistId,
        artist = artist,
        format = format,
        mimeType = mimeType,
        size = size,
        duration = duration,
        dateModified = dateModified,
        id = requireNotNull(id)
    )
}

fun Song.toSongRemovedEntity(): SongRemovedEntity {
    return SongRemovedEntity(
        fileId = fileId
    )
}