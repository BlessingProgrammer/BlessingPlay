package com.blessingsoftware.blessingplay.core.data.repository

import com.blessingsoftware.blessingplay.core.data.remote.api.MoreSongApi
import com.blessingsoftware.blessingplay.core.data.remote.model.DataResponse
import com.blessingsoftware.blessingplay.core.data.remote.model.UrlRequest
import com.blessingsoftware.blessingplay.core.domain.repository.MoreSongRepository
import okhttp3.ResponseBody

class MoreSongRepositoryImpl(
    private val moreSongApi: MoreSongApi
) : MoreSongRepository {
    override suspend fun postHandleUrl(request: UrlRequest): DataResponse {
        return moreSongApi.postHandleUrl(request)
    }

    override suspend fun downloadSong(path: String): ResponseBody {
        return moreSongApi.downloadSong(path)
    }
}