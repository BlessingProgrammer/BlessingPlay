package com.blessingsoftware.blessingplay.core.domain.model

data class Playlist(
    val id: Long = 0,
    var name: String,
    var thumbnail: String,
    var songCount: Long = 0
)

