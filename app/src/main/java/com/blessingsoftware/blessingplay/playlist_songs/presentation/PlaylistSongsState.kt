package com.blessingsoftware.blessingplay.playlist_songs.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Song

data class PlaylistSongsState(
    val songWithPositionLists: List<Song> = emptyList(),
    val dataLoading: Boolean = false,

    val revealedItemId: Long? = null,

    val songSelected: Song? = null,

    val isDeleteDialog: Boolean = false,

    val oldPosition: Long? = null,
)