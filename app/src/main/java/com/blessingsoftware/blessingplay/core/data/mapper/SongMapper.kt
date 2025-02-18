package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.local.SongEntity
import com.blessingsoftware.blessingplay.core.domain.model.Song

//fun Song.toSongEntityForUpsert(): SongEntity {
//    return SongEntity(
//        numericalOrder = numericalOrder,
//        fileId = fileId,
//        title = title,
//        fileName = fileName,
//        path = path,
//        albumArt = albumArt,
//        artistId = artistId,
//        artist = artist,
//        format = format,
//        mimeType = mimeType,
//        size = size,
//        duration = duration,
//        dateModified = dateModified,
//    )
//}
//
//fun Song.toSongEntityForUpdate(): SongEntity {
//    return SongEntity(
//        numericalOrder = numericalOrder,
//        fileId = fileId,
//        title = title,
//        fileName = fileName,
//        path = path,
//        albumArt = albumArt,
//        artistId = artistId,
//        artist = artist,
//        format = format,
//        mimeType = mimeType,
//        size = size,
//        duration = duration,
//        dateModified = dateModified,
//        id = id
//    )
//}
//
//fun Song.toSongEntityForDelete(): SongEntity {
//    return SongEntity(
//        numericalOrder = numericalOrder,
//        fileId = fileId,
//        title = title,
//        fileName = fileName,
//        path = path,
//        albumArt = albumArt,
//        artistId = artistId,
//        artist = artist,
//        format = format,
//        mimeType = mimeType,
//        size = size,
//        duration = duration,
//        dateModified = dateModified,
//        id = id
//    )
//}
//
//fun SongEntity.toSong(): Song {
//    return Song(
//        numericalOrder= numericalOrder,
//        fileId = fileId,
//        title = title,
//        fileName = fileName,
//        path = path,
//        albumArt = albumArt,
//        artistId = artistId,
//        artist = artist,
//        format = format,
//        mimeType = mimeType,
//        size = size,
//        duration = duration,
//        dateModified = dateModified,
//        id = requireNotNull(id)
//    )
//}