package com.blessingsoftware.blessingplay.core.data.mapper

import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistEntity
import com.blessingsoftware.blessingplay.core.domain.model.Playlist

fun Playlist.toPlaylistEntityForInsert(): PlaylistEntity {
    return PlaylistEntity(
        name = name,
        thumbnail = thumbnail
    )
}

fun Playlist.toPlaylistEntityForUpdate(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        name = name,
        thumbnail = thumbnail
    )
}

fun Playlist.toPlaylistEntityForDelete(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        name = name,
        thumbnail = thumbnail
    )
}

fun PlaylistEntity.toPlaylist(): Playlist {
    return Playlist(
        id = requireNotNull(id),
        name = name,
        thumbnail = thumbnail
    )
}