package com.blessingsoftware.blessingplay.core.data.remote.api

import com.blessingsoftware.blessingplay.core.data.remote.model.DataResponse
import com.blessingsoftware.blessingplay.core.data.remote.model.UrlRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoreSongApi {
    @POST("/more-song")
    suspend fun postHandleUrl(
        @Body request: UrlRequest
    ): DataResponse

    @GET("/more-song/{path}")
    suspend fun downloadSong(
        @Path("path") path: String
    ): ResponseBody
}

