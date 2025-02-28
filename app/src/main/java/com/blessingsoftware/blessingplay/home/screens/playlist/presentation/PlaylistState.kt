package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Playlist

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val dataLoading : Boolean = false,
    val pullLoading: Boolean = false,

    val revealedItemId: Long? = null,

    val playlistSelected: Playlist? = null,

    val isAddDialog: Boolean = false,
    val isUpdateDialog: Boolean = false,
    val isDeleteDialog: Boolean = false,

    val playlistName: String = "",
    val playlistThumbnail: String = ""
)
