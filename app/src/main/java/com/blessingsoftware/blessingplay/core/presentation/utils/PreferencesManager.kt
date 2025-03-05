package com.blessingsoftware.blessingplay.core.presentation.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
}

data class PlaybackSettings(
    val playlistType: Boolean,
    val playlistId: Long?,
    val lastSongId: Long?
)

class PreferencesManager(private val context: Context) {

    suspend fun savePlaybackSettings(
        playlistType: Boolean,
        playlistId: Long?,
        lastSongId: Long?,
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
        }
    }

    val playbackSettingsFlow: Flow<PlaybackSettings> = context.dataStore.data.map { preferences ->
        val playlistType = preferences[PreferencesKeys.PLAYLIST_TYPE] ?: false
        val playlistId = preferences[PreferencesKeys.PLAYLIST_ID]
        val lastSongId = preferences[PreferencesKeys.LAST_SONG_ID]

        PlaybackSettings(
            playlistType = playlistType,
            playlistId = playlistId,
            lastSongId = lastSongId
        )
    }
}