package com.blessingsoftware.blessingplay.home.screens.more_song.domain.use_case

import com.blessingsoftware.blessingplay.core.data.remote.model.DataResponse
import com.blessingsoftware.blessingplay.core.data.remote.model.UrlRequest
import com.blessingsoftware.blessingplay.core.domain.repository.MoreSongRepository
import com.blessingsoftware.blessingplay.home.screens.more_song.presentation.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostHandleUrl(
    private val moreSongRepository: MoreSongRepository
) {
    operator fun invoke(
        url: String?
    ): Flow<Resource<DataResponse>> = flow {
        coroutineScope {
            if (url.isNullOrBlank()) {
                emit(Resource.Error("Url is empty"))
                return@coroutineScope
            }

            val expectedTime = 10_000L
            val startTime = System.currentTimeMillis()

            val serverResult =  async  (Dispatchers.IO) {
                moreSongRepository.postHandleUrl(request = UrlRequest(url))
            }

            while (serverResult.isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = ((elapsed.toFloat() / expectedTime) * 95).coerceAtMost(95f).toInt()
                emit(Resource.Loading(progress))
                delay(100L)
            }

            try {
                val dataResponse = serverResult.await()
                emit(Resource.Success(dataResponse))
                emit(Resource.Loading(100))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.localizedMessage))
                emit(Resource.Loading(0))
            }
        }
    }
}