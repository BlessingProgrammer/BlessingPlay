package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.data.repository.MusicPlayerRepositoryImpl
import com.blessingsoftware.blessingplay.core.presentation.utils.RepeatModeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicPlayerRepository: MusicPlayerRepositoryImpl
) : ViewModel() {
    private val _musicPlayerState = MutableStateFlow(MusicPlayerState())
    val musicPlayerState = _musicPlayerState.asStateFlow()

    init {
        musicPlayerRepository.bindService()
        viewModelScope.launch {
            musicPlayerRepository.isBoundFlow.filter { it }.first()
            loadSongs()
        }
    }

    private fun loadSongs() {
        updateCurrentSong()
        updateMaxDuration()
        updateCurrentDuration()
        updateIsPlay()
        updateCurrentRepeatModeOption()
    }

    private fun updateCurrentSong() {
        viewModelScope.launch {
            musicPlayerRepository.currentSong.collectLatest { song ->
                _musicPlayerState.update {
                    it.copy(songSelected = song)
                }
            }
        }
    }

    private fun updateIsPlay() {
        viewModelScope.launch {
            musicPlayerRepository.isPlaying.collectLatest { playing ->
                _musicPlayerState.update {
                    it.copy(isPlaying = playing)
                }
            }
        }
    }

    private fun updateMaxDuration() {
        viewModelScope.launch {
            musicPlayerRepository.maxDuration.collectLatest { duration ->
                _musicPlayerState.update {
                    it.copy(maxDuration = duration)
                }
            }
        }
    }

    private fun updateCurrentDuration() {
        viewModelScope.launch {
            musicPlayerRepository.currentDuration.collectLatest { duration ->
                _musicPlayerState.update {
                    it.copy(currentDuration = duration)
                }
            }
        }
    }

    private fun updateCurrentRepeatModeOption() {
        viewModelScope.launch {
            musicPlayerRepository.currentRepeatOption.collectLatest { option ->
                _musicPlayerState.update {
                    it.copy(currentRepeatModeOption = option)
                }
            }
        }
    }

    fun prevSong() {
        musicPlayerRepository.prev()
    }

    fun playPauseMusic() {
        musicPlayerRepository.playPauseMusic()
    }

    fun nextSong() {
        musicPlayerRepository.next()
    }

    fun updateCurrentPosition(newPosition: Float) {
        _musicPlayerState.update {
            it.copy(currentDuration = newPosition)
        }
    }

    fun onSliderValueChanged(newPosition: Float) {
        musicPlayerRepository.seekTo(newPosition)
    }

    fun setRepeatModeOption(option: RepeatModeOption) {
        musicPlayerRepository.setRepeatModeOption(option)
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayerRepository.unbindService()
    }
}