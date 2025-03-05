package com.blessingsoftware.blessingplay.core.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.RepeatModeOption
import com.blessingsoftware.blessingplay.core.service.MusicPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _maxDuration = MutableStateFlow(0f)
    val maxDuration: StateFlow<Float> = _maxDuration

    private val _currentDuration = MutableStateFlow(0f)
    val currentDuration: StateFlow<Float> = _currentDuration

    private val _currentRepeatOption = MutableStateFlow<RepeatModeOption>(RepeatModeOption.OFF)
    val currentRepeatOption: StateFlow<RepeatModeOption> = _currentRepeatOption

    private val _isBoundFlow = MutableStateFlow(false)
    val isBoundFlow: StateFlow<Boolean> = _isBoundFlow

    private var musicPlayerService: MusicPlayerService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicPlayerService = binder.getService()
            isBound = true
            _isBoundFlow.value = true

            binder.getCurrentSong().onEach { song ->
                _currentSong.update { song }
            }.launchIn(CoroutineScope(Dispatchers.Main))

            binder.getIsPlaying().onEach { isPlaying ->
                _isPlaying.update { isPlaying }
            }.launchIn(CoroutineScope(Dispatchers.Main))

            binder.getMaxDuration().onEach { maxDuration ->
                _maxDuration.update { maxDuration }
            }.launchIn(CoroutineScope(Dispatchers.Main))

            binder.getCurrentDuration().onEach { currentDuration ->
                _currentDuration.update { currentDuration }
            }.launchIn(CoroutineScope(Dispatchers.Main))

            binder.getCurrentRepeatOption().onEach { option ->
                _currentRepeatOption.update { option }
            }.launchIn(CoroutineScope(Dispatchers.Main))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            _isBoundFlow.value = false
            musicPlayerService = null
        }
    }

    fun bindService() {
        val intent = Intent(context, MusicPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
            _isBoundFlow.value = false
        }
    }

    fun next() {
        musicPlayerService?.next()
    }

    fun playPauseMusic() {
        musicPlayerService?.playPause()
    }

    fun prev() {
        musicPlayerService?.prev()
    }

    fun setMusicList(songList: List<Song>) {
        musicPlayerService?.setMusicList(songList)
    }

    fun setCurrentSong(song: Song) {
        musicPlayerService?.setCurrentSong(song)
    }

    fun seekTo(sliderPosition: Float) {
        musicPlayerService?.seekTo(sliderPosition)
    }

    fun setRepeatModeOption(option: RepeatModeOption) {
        musicPlayerService?.setRepeatModeOption(option)
    }

    fun getCurrentSong(): Song? {
        return _currentSong.value
    }
}
