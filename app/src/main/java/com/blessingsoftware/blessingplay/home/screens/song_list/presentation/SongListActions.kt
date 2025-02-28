package com.blessingsoftware.blessingplay.home.screens.song_list.presentation

import com.blessingsoftware.blessingplay.core.domain.model.Song

sealed interface SongListActions {
    data class UpdateDataLoading(val isLoading: Boolean) : SongListActions
    data class UpdatePullLoading(val isLoading: Boolean) : SongListActions
    data class UpdateRevealedItemId(val songId: Long?) : SongListActions

    data class UpdateSongSelected(val songSelected: Song?) : SongListActions

    data class UpdateSearchQuery(val searchQuery: String) : SongListActions
    data class UpdateIndexListSearching(val indexList: List<Int>) : SongListActions

    data class UpdateIsUpdateDialog(val isUpdateDialog: Boolean) : SongListActions
    data class UpdateIsDeleteDialog(val isDeleteDialog: Boolean) : SongListActions
    data class UpdateIsExtraDialog(val isExtraDialog: Boolean) : SongListActions
    data class UpdateIsDetailDialog(val isDetailDialog: Boolean) : SongListActions

    data class UpdateSongTitle(val songTitle: String) : SongListActions
    data class UpdateSongArtist(val songArtist: String) : SongListActions

    data class UpdateIndex(val updateIndex: Int?) : SongListActions

    data class UpdateIsPlaylistDialog(val isPlaylistDialog: Boolean) : SongListActions

    data object LoadSongs : SongListActions
    data object PullToRefresh : SongListActions
    data object SearchingSong : SongListActions

    data object UpdateSong : SongListActions
    data object DeleteSong : SongListActions

    data object LoadPlaylists : SongListActions
}