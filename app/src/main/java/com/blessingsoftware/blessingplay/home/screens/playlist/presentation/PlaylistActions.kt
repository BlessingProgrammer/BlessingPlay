package com.blessingsoftware.blessingplay.home.screens.playlist.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Playlist

sealed interface PlaylistActions {
    data class UpdateDataLoading(val isLoading: Boolean) : PlaylistActions
    data class UpdatePullLoading(val isLoading: Boolean) : PlaylistActions
    data class UpdateRevealedItemId(val playlistId: Long?) : PlaylistActions

    data class UpdatePlaylistSelected(val playlistSelected: Playlist?) : PlaylistActions

    data class UpdateIsAddDialog(val isAddDialog: Boolean) : PlaylistActions
    data class UpdateIsUpdateDialog(val isUpdateDialog: Boolean) : PlaylistActions
    data class UpdateIsDeleteDialog(val isDeleteDialog: Boolean) : PlaylistActions

    data class UpdatePlaylistName(val playlistName: String) : PlaylistActions
    data class UpdatePlaylistThumbnail(val playlistThumbnail: String) : PlaylistActions

    data object LoadPlaylists : PlaylistActions
    data object PullToRefresh : PlaylistActions

    data object SavePlaylist : PlaylistActions
    data object UpdatePlaylist : PlaylistActions
    data object DeletePlaylist : PlaylistActions
}