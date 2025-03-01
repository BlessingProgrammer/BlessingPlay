package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.DeletePlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.GetAllPlaylistsWithSongCount
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.UpsertPlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.UpdatePlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getAllPlaylistsWithSongCount: GetAllPlaylistsWithSongCount,
    private val upsertPlayList: UpsertPlaylist,
    private val updatePlayList: UpdatePlaylist,
    private val deletePlayList: DeletePlaylist
) : ViewModel() {

    private val _playlistState = MutableStateFlow(PlaylistState())
    val playlistState = _playlistState.asStateFlow()

    private val _playlistChannel = Channel<Boolean>()
    val playlistFlow = _playlistChannel.receiveAsFlow()

    init {
        onAction(PlaylistActions.LoadPlaylists)
    }

    fun onAction(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.UpdateDataLoading ->
                _playlistState.update {
                    it.copy(dataLoading = action.isLoading)
                }

            is PlaylistActions.UpdatePullLoading ->
                _playlistState.update {
                    it.copy(pullLoading = action.isLoading)
                }

            is PlaylistActions.UpdateRevealedItemId ->
                _playlistState.update {
                    it.copy(revealedItemId = action.playlistId)
                }

            is PlaylistActions.UpdatePlaylistSelected ->
                _playlistState.update {
                    it.copy(playlistSelected = action.playlistSelected)
                }

            is PlaylistActions.UpdateIsAddDialog ->
                _playlistState.update {
                    it.copy(isAddDialog = action.isAddDialog)
                }

            is PlaylistActions.UpdateIsUpdateDialog ->
                _playlistState.update {
                    it.copy(isUpdateDialog = action.isUpdateDialog)
                }

            is PlaylistActions.UpdateIsDeleteDialog ->
                _playlistState.update {
                    it.copy(isDeleteDialog = action.isDeleteDialog)
                }

            is PlaylistActions.UpdatePlaylistName ->
                _playlistState.update {
                    it.copy(playlistName = action.playlistName)
                }

            is PlaylistActions.UpdatePlaylistThumbnail ->
                _playlistState.update {
                    it.copy(playlistThumbnail = action.playlistThumbnail)
                }

            PlaylistActions.LoadPlaylists ->
                viewModelScope.launch {
                    loadPlaylists()
                }

            PlaylistActions.PullToRefresh ->
                viewModelScope.launch {
                    pullToRefresh()
                }

            PlaylistActions.SavePlaylist ->
                viewModelScope.launch {
                    val isSaved = upsertPlaylist(
                        name = _playlistState.value.playlistName,
                        thumbnail = _playlistState.value.playlistThumbnail
                    )
                    _playlistChannel.send(isSaved)
                }

            PlaylistActions.UpdatePlaylist ->
                viewModelScope.launch {
                    _playlistState.value.playlistSelected?.let {
                        updatePlaylist(
                            playlist = it,
                            name = _playlistState.value.playlistName,
                            thumbnail = _playlistState.value.playlistThumbnail
                        )
                    }
                }

            PlaylistActions.DeletePlaylist ->
                viewModelScope.launch {
                    _playlistState.value.playlistSelected?.let {
                        deletePlaylist(it)
                    }
                }
        }
    }

    private suspend fun loadPlaylists() {
        onAction(PlaylistActions.UpdateDataLoading(true))
        _playlistState.update {
            it.copy(playlists = getAllPlaylistsWithSongCount.invoke())
        }
        delay(1000)
        onAction(PlaylistActions.UpdateDataLoading(false))
    }

    private suspend fun pullToRefresh() {
        onAction(PlaylistActions.UpdatePullLoading(true))
        onAction(PlaylistActions.LoadPlaylists)
        delay(300)
        onAction(PlaylistActions.UpdatePullLoading(false))
    }

    private suspend fun upsertPlaylist(
        name: String,
        thumbnail: String
    ): Boolean {
        return upsertPlayList.invoke(
            name = name,
            thumbnail = thumbnail
        )
    }

    private suspend fun updatePlaylist(
        playlist: Playlist,
        name: String,
        thumbnail: String
    ) {
        val updatedPlaylist = playlist.copy(name = name, thumbnail = thumbnail)
        updatePlayList.invoke(updatedPlaylist)
        onAction(PlaylistActions.UpdateIsUpdateDialog(false))
        onAction(PlaylistActions.UpdatePlaylistSelected(null))
        onAction(PlaylistActions.UpdateRevealedItemId(null))
        onAction(PlaylistActions.LoadPlaylists)
    }

    private suspend fun deletePlaylist(playlist: Playlist) {
        deletePlayList.invoke(playlist)
        onAction(PlaylistActions.UpdateIsDeleteDialog(false))
        onAction(PlaylistActions.UpdatePlaylistSelected(null))
        onAction(PlaylistActions.LoadPlaylists)
    }
}