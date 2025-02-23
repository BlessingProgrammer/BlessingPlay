package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.normalizeFirstChar
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.DeleteSong
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.InsertSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.UpdateSong
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
class SongListViewModel @Inject constructor(
    private val getAllSongs: GetAllSongs,
    private val insertSongs: InsertSongs,
    private val updateSong: UpdateSong,
    private val deleteSong: DeleteSong
) : ViewModel() {

    private val _songListState = MutableStateFlow<Map<Char, List<Song>>>(emptyMap())
    val songListState = _songListState.asStateFlow()

    private val _revealedItemId = MutableStateFlow<Long?>(null)
    val revealedItemId = _revealedItemId.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _songStateForUpdate = MutableStateFlow<Song?>(null)
    val songStateForUpdate = _songStateForUpdate.asStateFlow()

    private val _songStateForDelete = MutableStateFlow<Song?>(null)
    val songStateForDelete = _songStateForDelete.asStateFlow()

    private val _songStateForDetail = MutableStateFlow<Song?>(null)
    val songStateForDetail = _songStateForDetail.asStateFlow()

    private val _updateIndex = MutableStateFlow<Int?>(null)
    val updateIndex = _updateIndex.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _songListState.update {
                getAllSongs.invoke()
                    .sortedBy { it.title.trim() }
                    .groupBy {
                        val normalizedChar = normalizeFirstChar(it.title.trim().firstOrNull())
                        if (normalizedChar in 'A'..'Z') normalizedChar else '#'
                    }
            }
        }
    }

    fun pullToRefresh() {
        viewModelScope.launch {
            _isLoading.update { true }
            delay(1500)
            insertSongs.invoke()
            loadSongs()
            _isLoading.update { false }
        }
    }

    fun setRevealedItemId(itemId: Long?) {
        _revealedItemId.update { itemId }
    }

    fun setSongStateForUpdate(song: Song?) {
        _songStateForUpdate.update { song }
    }

    fun setSongStateForDelete(song: Song?) {
        _songStateForDelete.update { song }
    }

    fun setSongStateForDetail(id: Long?) {
        if (id != null) {
            val song = _songListState.value.values.flatten().find { it.id == id }
            _songStateForDetail.update { song }
        } else _songStateForDetail.update { null }
    }

    fun updateSong(song: Song, updateTitle: String, updateArtist: String) {
        viewModelScope.launch {
            val updatedSong = song.copy(title = updateTitle, artist = updateArtist)
            updateSong.invoke(updatedSong)
            val newHeader =
                normalizeFirstChar(updatedSong.title.trim().firstOrNull()).let {
                    if (it in 'A'..'Z') it else '#'
                }
            val currentMap = _songListState.value
            val headerToUpdate = currentMap.entries.find { entry ->
                entry.value.any { it.id == song.id }
            }?.key

            if (headerToUpdate != null) {
                val oldGroup =
                    currentMap[headerToUpdate]?.filter { it.id != song.id } ?: emptyList()
                if (headerToUpdate == newHeader) {
                    val newGroup = (oldGroup + updatedSong).sortedBy { it.title.trim() }
                    _songListState.update {
                        currentMap.toMutableMap().apply {
                            put(headerToUpdate, newGroup)
                        }
                    }
                } else {
                    val newOldGroup = oldGroup.sortedBy { it.title.trim() }
                    val newGroup = (currentMap[newHeader] ?: emptyList()) + updatedSong
                    _songListState.update {
                        currentMap.toMutableMap().apply {
                            put(headerToUpdate, newOldGroup)
                            put(newHeader, newGroup.sortedBy { it.title.trim() })
                        }
                    }
                }
            }
            _songStateForUpdate.update { null }
            _revealedItemId.update { null }
            _updateIndex.update {
                calculateScrollTargetIndexForSong(song.id)
            }
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            deleteSong.invoke(song)
            _songStateForDelete.update { null }
            _revealedItemId.update { null }
            loadSongs()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun calculateScrollTargetIndex(): List<Int> {
        val query = _searchQuery.value
        if (query.isBlank()) return emptyList()

        val indexes = mutableListOf<Int>()
        var index = -1

        for ((header, songs) in _songListState.value) {
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
        return indexes
    }

    private fun calculateScrollTargetIndexForSong(songId: Long): Int {
        var index = 0
        var found = false
        outer@ for ((header, songs) in _songListState.value) {
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
