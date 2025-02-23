package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    var name: String,
    var thumbnail: String?
)
