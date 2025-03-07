package com.blessingsoftware.blessingplay.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SongData(
    @SerializedName("song_path")
    val songPath : String
)
