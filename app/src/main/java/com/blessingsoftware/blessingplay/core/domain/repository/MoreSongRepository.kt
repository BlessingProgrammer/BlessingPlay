package com.blessingsoftware.blessingplay.core.domain.repository

import com.blessingsoftware.blessingplay.core.data.remote.model.DataResponse
import com.blessingsoftware.blessingplay.core.data.remote.model.UrlRequest
import okhttp3.ResponseBody

interface MoreSongRepository {
    suspend fun postHandleUrl(request: UrlRequest): DataResponse

    suspend fun downloadSong(path: String): ResponseBody
}