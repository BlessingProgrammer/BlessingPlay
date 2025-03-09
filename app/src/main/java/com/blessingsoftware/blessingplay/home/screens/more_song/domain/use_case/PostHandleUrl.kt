package com.blessingsoftware.blessingplay.home.screens.more_song.domain.use_case

import android.util.Log
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
        try {
            coroutineScope {
                if (url.isNullOrBlank()) {
                    emit(Resource.Error("Url is empty"))
                    return@coroutineScope
                }

                val expectedTime = 60000L
                val startTime = System.currentTimeMillis()

                val serverResult = async(Dispatchers.IO) {
                    moreSongRepository.postHandleUrl(request = UrlRequest(url))
                }

                while (System.currentTimeMillis() - startTime < expectedTime) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress = ((elapsed.toFloat() / expectedTime) * 95).coerceAtMost(95f).toInt()
                    emit(Resource.Loading(progress))
                    delay(100L)

                    if (elapsed >= 3000L && serverResult.isCompleted) {
                        break
                    }
                }

                val dataResponse = serverResult.await()
                delay(1500L)
                emit(Resource.Loading(100))
                delay(300L)
                emit(Resource.Success(dataResponse))
                emit(Resource.Loading(0))
            }
        } catch (e: java.net.SocketTimeoutException) {
            e.printStackTrace()
            emit(Resource.Error("Server error, please try again later"))
            emit(Resource.Loading(0))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Cannot connect to internet"))
            emit(Resource.Loading(0))
        }
    }
}