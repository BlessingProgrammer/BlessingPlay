package com.blessingsoftware.blessingplay.core.data.remote.dto

data class DataResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: SongData?
)
