package com.blessingsoftware.blessingplay.core.data.remote.model

import com.blessingsoftware.blessingplay.core.data.remote.dto.MoreSongDto

data class DataResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: MoreSongDto?
)
