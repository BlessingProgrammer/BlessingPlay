package com.blessingsoftware.blessingplay.playlist_songs.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Song

sealed interface PlaylistSongsActions {
    data class UpdateDataLoading(val isLoading: Boolean) : PlaylistSongsActions
    data class UpdateRevealedItemId(val songId: Long?) : PlaylistSongsActions

    data class UpdateSongSelected(val songSelected: Song?) : PlaylistSongsActions

    data class UpdateIsDeleteDialog(val isDeleteDialog: Boolean) : PlaylistSongsActions

    data class UpdateOldPosition(val position: Long?) : PlaylistSongsActions
}