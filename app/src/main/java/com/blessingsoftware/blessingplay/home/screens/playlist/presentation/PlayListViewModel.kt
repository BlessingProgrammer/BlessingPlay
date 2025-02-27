package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.DeletePlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.GetAllPlaylists
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.InsertPlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.UpdatePlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val getAllPlayList: GetAllPlaylists,
    private val insertPlayList: InsertPlaylist,
    private val updatePlayList: UpdatePlaylist,
    private val deletePlayList: DeletePlaylist
) : ViewModel() {

    private val _playlistListState = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistListState = _playlistListState.asStateFlow()

    private val _revealedItemId = MutableStateFlow<Long?>(null)
    val revealedItemId = _revealedItemId.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists(){
        viewModelScope.launch {
            _playlistListState.update {
                getAllPlayList.invoke()
            }
        }
    }



}