package com.blessingsoftware.blessingplay.core.presentation.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object PreferencesKeys {
    val PLAYLIST_TYPE = booleanPreferencesKey("playlist_type")
    val PLAYLIST_ID = longPreferencesKey("playlist_id")
    val LAST_SONG_ID = longPreferencesKey("last_song_id")
    val CURRENT_REPEAT_MODE = booleanPreferencesKey("current_repeat_mode")
    val CURRENT_SHUFFLE_STATUS = booleanPreferencesKey("current_shuffle_status")
}

data class PlaybackSettings(
    val playlistType: Boolean,
    val playlistId: Long?,
    val lastSongId: Long?,
    val currentRepeatMode: Boolean,
    val currentShuffleStatus: Boolean
)

class PreferencesManager(private val context: Context) {

    suspend fun savePlaybackSettings(
        playlistType: Boolean,
        playlistId: Long?,
        lastSongId: Long?,
        currentRepeatMode: Boolean,
        currentShuffleStatus: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYLIST_TYPE] = playlistType
            if (playlistId != null) {
                preferences[PreferencesKeys.PLAYLIST_ID] = playlistId
            } else {
                preferences.remove(PreferencesKeys.PLAYLIST_ID)
            }
            if (lastSongId != null) {
                preferences[PreferencesKeys.LAST_SONG_ID] = lastSongId
            } else {
                preferences.remove(PreferencesKeys.LAST_SONG_ID)
            }
            preferences[PreferencesKeys.CURRENT_REPEAT_MODE] = currentRepeatMode
            preferences[PreferencesKeys.CURRENT_SHUFFLE_STATUS] = currentShuffleStatus
        }
    }

    val playbackSettingsFlow: Flow<PlaybackSettings> = context.dataStore.data.map { preferences ->
        val playlistType = preferences[PreferencesKeys.PLAYLIST_TYPE] ?: false
        val playlistId = preferences[PreferencesKeys.PLAYLIST_ID]
        val lastSongId = preferences[PreferencesKeys.LAST_SONG_ID]
        val currentRepeatMode = preferences[PreferencesKeys.CURRENT_REPEAT_MODE] ?: false
        val currentShuffleStatus = preferences[PreferencesKeys.CURRENT_SHUFFLE_STATUS] ?: false

        PlaybackSettings(
            playlistType = playlistType,
            playlistId = playlistId,
            lastSongId = lastSongId,
            currentRepeatMode = currentRepeatMode,
            currentShuffleStatus = currentShuffleStatus
        )
    }
}