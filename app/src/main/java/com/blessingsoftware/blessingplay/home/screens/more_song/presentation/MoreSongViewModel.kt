package com.blessingsoftware.blessingplay.home.screens.more_song.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.data.mapper.toMoreSong
import com.blessingsoftware.blessingplay.home.screens.more_song.domain.use_case.DownloadSong

import com.blessingsoftware.blessingplay.home.screens.more_song.domain.use_case.PostHandleUrl
import com.blessingsoftware.blessingplay.home.screens.more_song.presentation.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreSongViewModel @Inject constructor(
    private val postHandleUrl: PostHandleUrl,
    private val downloadSong: DownloadSong
) : ViewModel() {

    private val _moreSongState = MutableStateFlow(MoreSongState())
    val moreSongState = _moreSongState.asStateFlow()

    fun onAction(action: MoreSongActions) {
        when (action) {
            is MoreSongActions.UpdateUrl ->
                _moreSongState.update { it.copy(url = action.url) }

            is MoreSongActions.UpdateIsLoading ->
                _moreSongState.update { it.copy(isLoading = action.isLoading) }

            is MoreSongActions.UpdateMoreSong ->
                _moreSongState.update { it.copy(moreSong = action.moreSong) }

            is MoreSongActions.UpdateCode ->
                _moreSongState.update { it.copy(code = action.code) }

            is MoreSongActions.UpdateMessage ->
                _moreSongState.update { it.copy(message = action.message) }

            is MoreSongActions.UpdateProgress ->
                _moreSongState.update { it.copy(progress = action.progress) }
        }
    }

    fun postUrl() {
        viewModelScope.launch {
            onAction(MoreSongActions.UpdateIsLoading(true))
            postHandleUrl.invoke(_moreSongState.value.url).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        onAction(MoreSongActions.UpdateProgress(progress = resource.progress ?: 0))
                    }

                    is Resource.Success -> {
                        val moreSong = resource.data.data?.toMoreSong()
                        onAction(MoreSongActions.UpdateMoreSong(moreSong = moreSong))
                        onAction(MoreSongActions.UpdateMessage(message = resource.data.message))
                        onAction(MoreSongActions.UpdateCode(code = resource.data.code))
                    }

                    is Resource.Error -> {
                        onAction(
                            MoreSongActions.UpdateMessage(
                                message = resource.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }
            onAction(MoreSongActions.UpdateIsLoading(false))
        }
    }

    fun downloadSongFile() {
        viewModelScope.launch {
            _moreSongState.value.moreSong?.let {
                onAction(MoreSongActions.UpdateIsLoading(true))
                downloadSong(it.songPath).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            onAction(
                                MoreSongActions.UpdateProgress(
                                    progress = resource.progress ?: 0
                                )
                            )
                        }

                        is Resource.Success -> {
                            val code = resource.data
                            if (code == 200) {
                                onAction(MoreSongActions.UpdateMessage(message = "Download successful"))
                                onAction(MoreSongActions.UpdateMoreSong(null))
                            }
                        }

                        is Resource.Error -> {
                            onAction(
                                MoreSongActions.UpdateMessage(
                                    message = resource.message ?: "Unknown error"
                                )
                            )
                        }
                    }
                }
                onAction(MoreSongActions.UpdateIsLoading(false))
            }
        }
    }
}