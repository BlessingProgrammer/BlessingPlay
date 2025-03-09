package com.blessingsoftware.blessingplay.home.screens.more_song.domain.use_case

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.blessingsoftware.blessingplay.core.domain.repository.MoreSongRepository
import com.blessingsoftware.blessingplay.home.screens.more_song.presentation.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DownloadSong(
    private val moreSongRepository: MoreSongRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(path: String?): Flow<Resource<Int>> = flow {
        try {
            coroutineScope {
                if (path.isNullOrBlank()) {
                    emit(Resource.Error("Path is empty"))
                    return@coroutineScope
                }

                val expectedTime = 60000L
                val startTime = System.currentTimeMillis()

                val serverResult = async(Dispatchers.Main) {
                    moreSongRepository.downloadSong(path)
                }

                while (System.currentTimeMillis() - startTime < expectedTime) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress =
                        ((elapsed.toFloat() / expectedTime) * 95).coerceAtMost(95f).toInt()
                    emit(Resource.Loading(progress))
                    delay(100L)

                    if (elapsed >= 3000L && serverResult.isCompleted) {
                        break
                    }
                }

                val dataResponse = serverResult.await()
                val fileUri = saveFileToMusic(dataResponse, path)

                if (fileUri != null) {
                    emit(Resource.Success(200))
                } else {
                    emit(Resource.Success(404))
                    emit(Resource.Error("Failed to save file"))
                }
                delay(1500L)
                emit(Resource.Loading(100))
                delay(300L)
                emit(Resource.Loading(0))
            }
        } catch (e: java.net.SocketTimeoutException) {
            emit(Resource.Error("Server error, please try again later"))
            emit(Resource.Loading(0))
        } catch (e: Exception) {
            emit(Resource.Error("Cannot connect to internet"))
            emit(Resource.Loading(0))
        }
    }

    private fun saveFileToMusic(responseBody: okhttp3.ResponseBody, fileName: String): Uri? {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            }
        }

        val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                responseBody.byteStream().copyTo(outputStream)
            }
        }
        return uri
    }
}