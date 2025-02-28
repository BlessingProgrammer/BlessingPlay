package com.blessingsoftware.blessingplay.core.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId","songId"])
data class PlaylistSongCrossRefEntity(
    val playlistId:Long,
    val songId: Long,
    val position: Long
)
