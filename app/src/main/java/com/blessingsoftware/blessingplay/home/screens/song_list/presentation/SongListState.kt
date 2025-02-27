package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Song

data class SongListState(
    val songList: Map<Char, List<Song>> = emptyMap(),
    val revealedItemId: Long? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val songStateForUpdate: Song? = null,
    val songStateForDelete: Song? = null,
    val songStateForDetail: Song? = null,
    val updateIndex: Int? = null
)
