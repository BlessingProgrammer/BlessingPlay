package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Playlist
import com.blessingsoftware.blessingplay.core.domain.model.Song

data class SongListState(
    val songLists: Map<Char, List<Song>> = emptyMap(),
    val dataLoading : Boolean = false,
    val pullLoading: Boolean = false,

    val revealedItemId: Long? = null,

    val songSelected: Song? = null,

    val searchQuery: String = "",
    val indexListSearching: List<Int> = emptyList(),

    val isUpdateDialog: Boolean = false,
    val isDeleteDialog: Boolean = false,
    val isExtraDialog: Boolean = false,
    val isDetailDialog: Boolean = false,

    val songTitle: String = "",
    val songArtist: String = "",

    val updateIndex: Int? = null,

    val playlist: List<Playlist> = emptyList(),
    val isPlaylistDialog: Boolean = false
)
