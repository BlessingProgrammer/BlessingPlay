package com.blessingsoftware.blessingplay.core.presentation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Home : Screen
}