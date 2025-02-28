package com.blessingsoftware.blessingplay.splash.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.blessingplay.core.presentation.utils.isDatabaseExist
import com.blessingsoftware.blessingplay.splash.domain.use_case.LoadMediaFileAndSaveToDb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loadMediaFileAndSaveToDb: LoadMediaFileAndSaveToDb,
    private val application: Application
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMedias()
    }

    private fun loadMedias() {
        viewModelScope.launch {
            if (isDatabaseExist(application)) {
                delay(2000)
            } else loadMediaFileAndSaveToDb()
            _isLoading.value = false
        }
    }
}