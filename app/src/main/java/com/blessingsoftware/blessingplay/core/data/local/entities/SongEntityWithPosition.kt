package com.blessingsoftware.blessingplay.core.data.local.entities

import androidx.room.Embedded

data class SongEntityWithPosition(
    @Embedded val songEntity: SongEntity,
    val position: Long
)
