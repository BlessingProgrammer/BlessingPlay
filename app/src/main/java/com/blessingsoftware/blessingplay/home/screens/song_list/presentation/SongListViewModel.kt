package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.data.repository.MusicPlayerRepositoryImpl
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.PreferencesManager
import com.blessingsoftware.blessingplay.core.presentation.utils.normalizeFirstChar
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.AddSongToPlaylist
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.DeleteSong
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllPlaylists
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.InsertSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.UpdateSong
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getAllSongs: GetAllSongs,
    private val getAllPlaylists: GetAllPlaylists,
    private val insertSongs: InsertSongs,
    private val updateSong: UpdateSong,
    private val deleteSong: DeleteSong,
    private val addSongToPlaylist: AddSongToPlaylist,
    private val musicPlayerRepository: MusicPlayerRepositoryImpl,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _songListState = MutableStateFlow(SongListState())
    val songListState = _songListState.asStateFlow()

    private val preferencesManager = PreferencesManager(context)

    init {
        onAction(SongListActions.LoadSongs)
    }

    fun onAction(action: SongListActions) {
        when (action) {
            is SongListActions.UpdateDataLoading ->
                _songListState.update {
                    it.copy(dataLoading = action.isLoading)
                }

            is SongListActions.UpdatePullLoading ->
                _songListState.update {
                    it.copy(pullLoading = action.isLoading)
                }

            is SongListActions.UpdateRevealedItemId ->
                _songListState.update {
                    it.copy(revealedItemId = action.songId)
                }

            is SongListActions.UpdateSongSelected ->
                _songListState.update {
                    it.copy(songSelected = action.songSelected)
                }

            is SongListActions.UpdateSearchQuery ->
                _songListState.update {
                    it.copy(searchQuery = action.searchQuery)
                }

            is SongListActions.UpdateIndexListSearching ->
                _songListState.update {
                    it.copy(indexListSearching = action.indexList)
                }

            is SongListActions.UpdateIsUpdateDialog ->
                _songListState.update {
                    it.copy(isUpdateDialog = action.isUpdateDialog)
                }

            is SongListActions.UpdateIsDeleteDialog ->
                _songListState.update {
                    it.copy(isDeleteDialog = action.isDeleteDialog)
                }

            is SongListActions.UpdateIsDetailDialog ->
                _songListState.update {
                    it.copy(isDetailDialog = action.isDetailDialog)
                }

            is SongListActions.UpdateIsExtraDialog ->
                _songListState.update {
                    it.copy(isExtraDialog = action.isExtraDialog)
                }

            is SongListActions.UpdateSongTitle ->
                _songListState.update {
                    it.copy(songTitle = action.songTitle)
                }

            is SongListActions.UpdateSongArtist ->
                _songListState.update {
                    it.copy(songArtist = action.songArtist)
                }

            is SongListActions.UpdateIndex ->
                _songListState.update {
                    it.copy(updateIndex = action.updateIndex)
                }

            is SongListActions.UpdateIsPlaylistDialog ->
                _songListState.update {
                    it.copy(isPlaylistDialog = action.isPlaylistDialog)
                }

            SongListActions.LoadSongs ->
                viewModelScope.launch {
                    loadSongs()
                }

            SongListActions.PullToRefresh ->
                viewModelScope.launch {
                    pullToRefresh()
                }

            SongListActions.SearchingSong ->
                searchingSong(_songListState.value.searchQuery)


            SongListActions.UpdateSong ->
                viewModelScope.launch {
                    _songListState.value.songSelected?.let {
                        updateSong(
                            song = it,
                            updateTitle = songListState.value.songTitle,
                            updateArtist = songListState.value.songArtist
                        )
                    }
                }

            SongListActions.DeleteSong ->
                viewModelScope.launch {
                    _songListState.value.songSelected?.let {
                        deleteSong(it)
                    }
                }

            SongListActions.LoadPlaylists ->
                viewModelScope.launch {
                    loadPlaylist()
                }
        }
    }

    private suspend fun loadSongs() {
        onAction(SongListActions.UpdateDataLoading(true))
        _songListState.update {
            it.copy(songLists = getAllSongs.invoke()
                .sortedBy { it.title.trim() }
                .groupBy {
                    val normalizedChar = normalizeFirstChar(it.title.trim().firstOrNull())
                    if (normalizedChar in 'A'..'Z') normalizedChar else '#'
                })
        }
        delay(1000)
        onAction(SongListActions.UpdateDataLoading(false))
    }

    private suspend fun pullToRefresh() {
        onAction(SongListActions.UpdatePullLoading(true))
        insertSongs.invoke()
        onAction(SongListActions.LoadSongs)
        delay(300)
        onAction(SongListActions.UpdatePullLoading(false))
    }

    private suspend fun loadPlaylist() {
        _songListState.update {
            it.copy(playlist = getAllPlaylists.invoke().sortedBy { it.name.trim() })
        }
    }

    private fun searchingSong(query: String) {
        if (query.isBlank()) {
            onAction(SongListActions.UpdateIndexListSearching(emptyList()))
            return
        }

        val indexes = mutableListOf<Int>()
        var index = -1

        for ((header, songs) in _songListState.value.songLists) {
            index++

            if (header.toString().contains(query, ignoreCase = true)) {
                indexes.add(index)
            }

            for (song in songs) {
                index++
                if (song.title.contains(query, ignoreCase = true)
                    || song.artist.contains(query, ignoreCase = true)
                ) {
                    indexes.add(index)
                }
            }
        }
        onAction(SongListActions.UpdateIndexListSearching(indexes))
    }

    private suspend fun updateSong(song: Song, updateTitle: String, updateArtist: String) {
        val updatedSong = song.copy(title = updateTitle, artist = updateArtist)
        updateSong.invoke(updatedSong)
        val newHeader =
            normalizeFirstChar(updatedSong.title.trim().firstOrNull()).let {
                if (it in 'A'..'Z') it else '#'
            }
        val currentMap = _songListState.value.songLists
        val headerToUpdate = currentMap.entries.find { entry ->
            entry.value.any { it.id == song.id }
        }?.key

        if (headerToUpdate != null) {
            val oldGroup =
                currentMap[headerToUpdate]?.filter { it.id != song.id } ?: emptyList()
            if (headerToUpdate == newHeader) {
                val newGroup = (oldGroup + updatedSong).sortedBy { it.title.trim() }
                _songListState.update {
                    it.copy(songLists = currentMap.toMutableMap().apply {
                        put(headerToUpdate, newGroup)
                    })
                }
            } else {
                val newOldGroup = oldGroup.sortedBy { it.title.trim() }
                val newGroup = (currentMap[newHeader] ?: emptyList()) + updatedSong
                _songListState.update {
                    it.copy(songLists = currentMap.toMutableMap().apply {
                        put(headerToUpdate, newOldGroup)
                        put(newHeader, newGroup.sortedBy { it.title.trim() })
                    })
                }
            }
        }
        onAction(SongListActions.UpdateIsUpdateDialog(false))
        onAction(SongListActions.UpdateSongSelected(null))
        onAction(SongListActions.UpdateRevealedItemId(null))
        onAction(SongListActions.UpdateIndex(calculateScrollTargetIndexForSong(updatedSong.id)))
    }

    private suspend fun deleteSong(song: Song) {
        deleteSong.invoke(song)
        onAction(SongListActions.UpdateIsDeleteDialog(false))
        onAction(SongListActions.UpdateSongSelected(null))
        onAction(SongListActions.LoadSongs)
    }

    fun addSongToPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val result = _songListState.value.songSelected?.let {
                addSongToPlaylist(
                    playlistId = playlistId,
                    songId = it.id
                )
            }
            if (result == true) {
                onAction(SongListActions.UpdateIsPlaylistDialog(false))
            }
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            val list: List<Song> = _songListState.value.songLists.values.flatten()
            musicPlayerRepository.setMusicList(list)
            musicPlayerRepository.setCurrentSong(song)
            preferencesManager.savePlaybackSettings(
                playlistType = false,
                playlistId = null,
                lastSongId = song.id
            )
            onAction(SongListActions.UpdateRevealedItemId(null))
            onAction(SongListActions.UpdateIsExtraDialog(false))
            onAction(SongListActions.UpdateSongSelected(null))
        }
    }

    private fun calculateScrollTargetIndexForSong(songId: Long): Int {
        var index = 0
        var found = false
        outer@ for ((header, songs) in _songListState.value.songLists) {
            index++
            for (song in songs) {
                if (song.id == songId) {
                    found = true
                    break@outer
                }
                index++
            }
        }
        return if (found) index else 0
    }
}
