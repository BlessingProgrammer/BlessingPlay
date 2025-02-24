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

    private val _songListState = MutableStateFlow(SongListState())
    val songListState = _songListState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _songListState.update {
                it.copy(songList = getAllSongs.invoke()
                    .sortedBy { it.title.trim() }
                    .groupBy {
                        val normalizedChar = normalizeFirstChar(it.title.trim().firstOrNull())
                        if (normalizedChar in 'A'..'Z') normalizedChar else '#'
                    })
            }
        }
    }

    fun pullToRefresh() {
        viewModelScope.launch {
            _songListState.update {
                it.copy(isLoading = true)
            }
            delay(1500)
            insertSongs.invoke()
            loadSongs()
            _songListState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun setRevealedItemId(itemId: Long?) {
        _songListState.update {
            it.copy(revealedItemId = itemId)
        }
    }

    fun setSongStateForUpdate(song: Song?) {
        _songListState.update {
            it.copy(songStateForUpdate = song)
        }
    }

    fun setSongStateForDelete(song: Song?) {
        _songListState.update {
            it.copy(songStateForDelete = song)
        }
    }

    fun setSongStateForDetail(id: Long?) {
        if (id != null) {
            val song = _songListState.value.songList.values.flatten().find { it.id == id }
            _songListState.update {
                it.copy(songStateForDetail = song)
            }
        } else _songListState.update {
            it.copy(songStateForDetail = null)
        }
    }

    fun updateSong(song: Song, updateTitle: String, updateArtist: String) {
        viewModelScope.launch {
            val updatedSong = song.copy(title = updateTitle, artist = updateArtist)
            updateSong.invoke(updatedSong)
            val newHeader =
                normalizeFirstChar(updatedSong.title.trim().firstOrNull()).let {
                    if (it in 'A'..'Z') it else '#'
                }
            val currentMap = _songListState.value.songList
            val headerToUpdate = currentMap.entries.find { entry ->
                entry.value.any { it.id == song.id }
            }?.key

            if (headerToUpdate != null) {
                val oldGroup =
                    currentMap[headerToUpdate]?.filter { it.id != song.id } ?: emptyList()
                if (headerToUpdate == newHeader) {
                    val newGroup = (oldGroup + updatedSong).sortedBy { it.title.trim() }
                    _songListState.update {
                        it.copy(songList = currentMap.toMutableMap().apply {
                            put(headerToUpdate, newGroup)
                        })
                    }
                } else {
                    val newOldGroup = oldGroup.sortedBy { it.title.trim() }
                    val newGroup = (currentMap[newHeader] ?: emptyList()) + updatedSong
                    _songListState.update {
                        it.copy(songList = currentMap.toMutableMap().apply {
                            put(headerToUpdate, newOldGroup)
                            put(newHeader, newGroup.sortedBy { it.title.trim() })
                        })
                    }
                }
            }
            _songListState.update {
                it.copy(songStateForUpdate = null)
            }
            _songListState.update {
                it.copy(revealedItemId = null)
            }
            _songListState.update {
                it.copy(updateIndex = calculateScrollTargetIndexForSong(updatedSong.id))
            }
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            deleteSong.invoke(song)
            _songListState.update {
                it.copy(songStateForDelete = null)
            }
            _songListState.update {
                it.copy(revealedItemId = null)
            }
            loadSongs()
        }
    }

    fun setSearchQuery(query: String) {
        _songListState.update {
            it.copy(searchQuery = query)
        }
    }

    fun calculateScrollTargetIndex(): List<Int> {
        val query = _songListState.value.searchQuery
        if (query.isBlank()) return emptyList()

        val indexes = mutableListOf<Int>()
        var index = -1

        for ((header, songs) in _songListState.value.songList) {
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
        outer@ for ((header, songs) in _songListState.value.songList) {
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
