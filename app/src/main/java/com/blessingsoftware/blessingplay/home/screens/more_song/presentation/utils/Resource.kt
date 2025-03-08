package com.blessingsoftware.blessingplay.home.screens.more_song.presentation.utils

sealed class Resource<out T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error(val message: String? = null) : Resource<Nothing>()
    class Loading(val progress: Int? = null) : Resource<Nothing>()
}