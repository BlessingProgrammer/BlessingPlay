package com.blessingsoftware.blessingplay.home.presentation

import kotlinx.serialization.Serializable

sealed interface BottomNavScreen {
    @Serializable
    data object SongList : BottomNavScreen

    @Serializable
    data object Playlist : BottomNavScreen

    @Serializable
    data object MusicPlayer : BottomNavScreen

    @Serializable
    data object Album : BottomNavScreen

    @Serializable
    data object Setting : BottomNavScreen
}