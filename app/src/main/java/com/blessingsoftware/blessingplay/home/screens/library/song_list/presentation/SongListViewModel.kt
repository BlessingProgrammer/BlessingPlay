package com.blessingsoftware.blessingplay.home.screens.library.song_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.presentation.utils.normalizeFirstChar
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.DeleteSong
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.GetAllSongs
import com.blessingsoftware.blessingplay.splash.domain.use_case.LoadMediaFileAndSaveToMemory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getAllSongs: GetAllSongs,
    private val deleteSong: DeleteSong,
    private val loadMediaFileAndSaveToMemory: LoadMediaFileAndSaveToMemory
) : ViewModel() {

    private val _songListState = MutableStateFlow<Map<Char, List<Song>>>(emptyMap())
    val songListState = _songListState.asStateFlow()

    private val _revealedItemId = MutableStateFlow<String?>(null)
    val revealedItemId = _revealedItemId.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            val groupedSongs = getAllSongs.invoke()
                .sortedBy { it.title }
                .groupBy {
                    val normalizedChar = normalizeFirstChar(it.title.firstOrNull())
                    if (normalizedChar in 'A'..'Z') normalizedChar else '#'
                }
            _songListState.value = groupedSongs
        }
    }

    fun pullToRefresh() {
        viewModelScope.launch {
            _isLoading.value = true
            loadMediaFileAndSaveToMemory()
            loadSongs()
            _isLoading.value = false
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            deleteSong.invoke(song)
            loadSongs()
        }
    }

    fun setRevealedItemId(itemId: String?) {
        viewModelScope.launch {
            _revealedItemId.value = itemId
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun calculateScrollTargetIndex(): Int {
        val query = _searchQuery.value
        if (query.isBlank()) return 0

        var targetIndex = 0
        var found = false
        outer@ for ((header, songs) in _songListState.value) {
            targetIndex++
            for (song in songs) {
                if (song.title.contains(query, ignoreCase = true)
                    || song.artist.contains(query, ignoreCase = true)
                    || header.toString().contains(query, ignoreCase = true)
                ) {
                    found = true
                    break@outer
                }
                targetIndex++
            }
        }
        return if (found) targetIndex else 0
    }


}
