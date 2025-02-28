package com.blessingsoftware.blessingplay.core.domain.model

data class Song(
    val id: Long,
    val fileId: Long,
    var title: String,
    val fileName: String,
    val path: String,
    val albumArt: String?,
    val artistId: Int,
    var artist: String,
    val format: String,
    val mimeType: String,
    val size: Long,
    val duration: Long,
    val dateModified: Long,
    val position: Long? = null
)





