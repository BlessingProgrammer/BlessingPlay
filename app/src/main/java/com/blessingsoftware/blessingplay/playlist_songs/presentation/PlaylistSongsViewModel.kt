package com.blessingsoftware.blessingplay.playlist_songs.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.data.repository.MusicPlayerRepositoryImpl
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.PreferencesManager
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.DeleteSongFromPlaylist
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.GetAllSongsFromPlaylist
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.UpdateSongPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    private val getAllSongsFromPlaylist: GetAllSongsFromPlaylist,
    private val updateSongPosition: UpdateSongPosition,
    private val deleteSongFromPlaylist: DeleteSongFromPlaylist,
    private val musicPlayerRepository: MusicPlayerRepositoryImpl,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _playlistSongState = MutableStateFlow(PlaylistSongsState())
    val playlistSongsState = _playlistSongState.asStateFlow()

    private val _playlistSongChannel = Channel<Boolean>()
    val playlistSongFlow = _playlistSongChannel.receiveAsFlow()

    private val preferencesManager = PreferencesManager(context)

    fun onAction(action: PlaylistSongsActions) {
        when (action) {
            is PlaylistSongsActions.UpdateDataLoading ->
                _playlistSongState.update {
                    it.copy(dataLoading = action.isLoading)
                }

            is PlaylistSongsActions.UpdateRevealedItemId ->
                _playlistSongState.update {
                    it.copy(revealedItemId = action.songId)
                }

            is PlaylistSongsActions.UpdateSongSelected ->
                _playlistSongState.update {
                    it.copy(songSelected = action.songSelected)
                }

            is PlaylistSongsActions.UpdateIsDeleteDialog ->
                _playlistSongState.update {
                    it.copy(isDeleteDialog = action.isDeleteDialog)
                }

            is PlaylistSongsActions.UpdateOldPosition ->
                _playlistSongState.update {
                    it.copy(oldPosition = action.position)
                }
        }
    }

    fun loadSongWithPositionLists(playlistId: Long) {
        viewModelScope.launch {
            onAction(PlaylistSongsActions.UpdateDataLoading(true))
            _playlistSongState.update {
                it.copy(songWithPositionLists = getAllSongsFromPlaylist.invoke(playlistId)
                    .sortedBy { it.position })
            }
            delay(300)
            onAction(PlaylistSongsActions.UpdateDataLoading(false))
        }
    }

    fun moveSongPositionLocally(songId: Long, newPosition: Long) {
        viewModelScope.launch {
            val currentList = _playlistSongState.value.songWithPositionLists.toMutableList()
            val fromIndex = currentList.indexOfFirst { it.id == songId }
            if (fromIndex == -1) return@launch

            val movedItem = currentList.removeAt(fromIndex)

            val targetIndex = newPosition.toInt().coerceIn(0, currentList.size)

            currentList.add(targetIndex, movedItem)

            _playlistSongState.update { state ->
                state.copy(songWithPositionLists = currentList)
            }
        }
    }

    fun updateSongPosition(playlistId: Long, songId: Long, newPosition: Long) {
        viewModelScope.launch {
            _playlistSongState.value.oldPosition?.let {
                updateSongPosition.invoke(
                    playlistId = playlistId,
                    songId = songId,
                    oldPosition = it,
                    newPosition = newPosition
                )
                loadSongWithPositionLists(playlistId)
            }
        }
    }

    fun deleteSongPosition(playlistId: Long) {
        viewModelScope.launch {
            _playlistSongState.value.songSelected?.let {
                it.position?.let { it1 ->
                    deleteSongFromPlaylist.invoke(
                        playlistId = playlistId,
                        songId = it.id,
                        position = it1
                    )
                    _playlistSongChannel.send(true)
                }
            }
        }
    }

    fun playPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch {
            val list = getAllSongsFromPlaylist.invoke((playlistId)).sortedBy { it.position }
            musicPlayerRepository.setMusicList(list)
            musicPlayerRepository.setCurrentSong(song)
            preferencesManager.savePlaybackSettings(
                playlistType = true,
                playlistId = playlistId,
                lastSongId = song.id
            )
        }
    }
}