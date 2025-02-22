package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SongRemovedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val fileId: Long
)