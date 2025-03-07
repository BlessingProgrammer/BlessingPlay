package com.blessingsoftware.blessingplay.home.screens.more_song.presentation.utils

sealed class Resource<T>(
    val data: T? = null
) {
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(data: T? = null) : Resource<T>(data = data)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(data = null)
}