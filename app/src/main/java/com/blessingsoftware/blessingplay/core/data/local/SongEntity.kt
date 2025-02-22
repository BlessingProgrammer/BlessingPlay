package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val fileId: Long,
    var title: String,
    val fileName: String,
    val path: String,
    val albumArt:String?,
    val artistId: Int,
    var artist: String,
    val format: String,
    val mimeType: String,
    val size: Long,
    val duration: Long,
    val dateModified: Long
)