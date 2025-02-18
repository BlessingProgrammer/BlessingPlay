package com.blessingsoftware.blessingplay.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.splash.domain.use_case.LoadMediaFileAndSaveToMemory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loadMediaFileAndSaveToMemory: LoadMediaFileAndSaveToMemory
) : ViewModel() {
    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMedias()
    }

    private fun loadMedias() {
        viewModelScope.launch {
            loadMediaFileAndSaveToMemory()
            _isLoading.value = false
        }
    }
}