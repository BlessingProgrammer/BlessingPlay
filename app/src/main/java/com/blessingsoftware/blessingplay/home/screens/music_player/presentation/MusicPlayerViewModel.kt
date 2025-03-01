package com.blessingsoftware.blessingplay.home.screens.music_player.presentation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val getAllSongs: GetAllSongs
) : ViewModel() {

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState())
    val musicPlayerState = _musicPlayerState.asStateFlow()

    init {
        loadSongs()
    }

    fun onAction(action: MusicPlayerActions) {
        when (action) {
            is MusicPlayerActions.UpdateSongSelected ->
                _musicPlayerState.update {
                    it.copy(songSelected = action.song)
                }

            is MusicPlayerActions.UpdateIsPlaying ->
                _musicPlayerState.update {
                    it.copy(isPlaying = action.isPlaying)
                }

            is MusicPlayerActions.UpdateExoPlayer ->
                _musicPlayerState.update {
                    it.copy(exoPlayer = action.exoPlayer)
                }

            is MusicPlayerActions.UpdateSliderPosition ->
                _musicPlayerState.update {
                    it.copy(sliderPosition = action.position)
                }

            is MusicPlayerActions.UpdateTotalDuration ->
                _musicPlayerState.update {
                    it.copy(totalDuration = action.duration)
                }
        }
    }

    fun loadSongs() {
        viewModelScope.launch {
            _musicPlayerState.update {
                it.copy(songLists = getAllSongs.invoke())
            }
            setSongSelected(_musicPlayerState.value.songLists.first())
        }
    }

    fun setSongSelected(song: Song?) {
        _musicPlayerState.update {
            it.copy(songSelected = song)
        }
    }
}