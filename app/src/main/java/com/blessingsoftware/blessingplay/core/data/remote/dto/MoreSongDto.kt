package com.blessingsoftware.blessingplay.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MoreSongDto(
    @SerializedName("song_path")
    val songPath : String
)
