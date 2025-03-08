package com.blessingsoftware.blessingplay.core.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.blessingsoftware.blessingplay.CHANNEL_ID
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.Player
import com.blessingsoftware.blessingplay.core.data.local.AppDb
import com.blessingsoftware.blessingplay.core.data.mapper.listSongEntityToListSong
import com.blessingsoftware.blessingplay.core.data.mapper.listSongEntityWithPositionToListSong
import com.blessingsoftware.blessingplay.core.data.mapper.toSong
import com.blessingsoftware.blessingplay.core.presentation.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

const val PREV = "prev"
const val NEXT = "next"
const val PLAY_PAUSE = "play_pause"

@AndroidEntryPoint
class MusicPlayerService : Service() {

    companion object {
        @Volatile
        var isServiceRunning = false
    }

    @Inject
    lateinit var appDb: AppDb

    private lateinit var exoPlayer: ExoPlayer

    private val songList = MutableStateFlow<List<Song>>(emptyList())
    private val currentSong = MutableStateFlow<Song?>(null)
    private val isPlaying = MutableStateFlow(false)

    private val maxDuration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)

    private val currentRepeatOption = MutableStateFlow(false)
    private val currentShuffleStatus = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.Main)

    private var job: Job? = null

    private lateinit var preferencesManager: PreferencesManager

    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService() = this@MusicPlayerService

        fun getCurrentSong() = currentSong
        fun getIsPlaying() = isPlaying

        fun getMaxDuration() = maxDuration
        fun getCurrentDuration() = currentDuration

        fun getCurrentRepeatOption() = currentRepeatOption

        fun getCurrentShuffleStatus() = currentShuffleStatus
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        preferencesManager = PreferencesManager(applicationContext)
        loadPlaylistSettings()

        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.localConfiguration?.tag?.let { tag ->
                    val song = songList.value.find { it.id == tag }
                    if (song != null) {
                        currentSong.update { song }
                        maxDuration.update { song.duration.toFloat() }
                        isPlaying.update { exoPlayer.isPlaying }
                        createNotification(song)
                        saveCurrentPlaybackStatus()
                    }
                }
            }
        })

//        exoPlayer.addListener(object : Player.Listener {
//            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
//                Log.d("ExoPlaying", "onIsPlayingChanged: $isPlayingNow")
//                isPlaying.update { isPlayingNow }
//            }
//        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {
                PREV -> {
                    prev()
                }

                PLAY_PAUSE -> {
                    playPause()
                }

                NEXT -> {
                    next()
                }
            }
        }
        return START_STICKY
    }

    private fun loadPlaylistSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val settings = preferencesManager.playbackSettingsFlow.first()
            if (settings.playlistType) {
                val playlistId = settings.playlistId ?: 0L
                val playlistSongEntities =
                    appDb.playlistSongCrossRefDao.getSongEntitiesWithPositionInPlaylist(playlistId)
                val songs: List<Song> = listSongEntityWithPositionToListSong(playlistSongEntities)
                songList.update { songs }
            } else {
                val allSongEntities = appDb.songDao.getAllSongEntities()
                val songs: List<Song> = listSongEntityToListSong(allSongEntities)
                songList.update { songs }
            }
            if (settings.lastSongId == null) {
                currentSong.update { songList.value.firstOrNull() }
            } else {
                val song = appDb.songDao.getSongEntityBySongId(settings.lastSongId)
                currentSong.update { song.toSong() }
            }

            currentRepeatOption.update { settings.currentRepeatMode }
            currentShuffleStatus.update { settings.currentShuffleStatus }

            withContext(Dispatchers.Main) {
                when (settings.currentRepeatMode) {
                    true -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                    false -> exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                }
                exoPlayer.shuffleModeEnabled = settings.currentShuffleStatus
                start()
            }
        }
    }

    private fun saveCurrentPlaybackStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentSettings = preferencesManager.playbackSettingsFlow.first()
            currentSong.value?.let { song ->
                preferencesManager.savePlaybackSettings(
                    playlistType = currentSettings.playlistType,
                    playlistId = currentSettings.playlistId,
                    lastSongId = song.id,
                    currentRepeatMode = currentSettings.currentRepeatMode,
                    currentShuffleStatus = currentSettings.currentShuffleStatus
                )
            }
        }
    }

    fun setCurrentSong(song: Song) {
        currentSong.update { song }
        play()
    }

    fun setMusicList(list: List<Song>) {
        songList.update { list }
    }

    fun prev() {
        if (exoPlayer.mediaItemCount == 0 || songList.value.isEmpty()) return
        job?.cancel()

        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
            exoPlayer.play()
        } else {
            val lastIndex = exoPlayer.mediaItemCount - 1
            if (lastIndex >= 0) {
                exoPlayer.seekTo(lastIndex, 0)
                exoPlayer.play()
            }
        }

        isPlaying.update { true }
        updateDurations()
        saveCurrentPlaybackStatus()
        currentSong.value?.let { createNotification(it) }
    }

    fun playPause() {
        if (exoPlayer.mediaItemCount == 0 || songList.value.isEmpty()) return
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            isPlaying.update { false }
        } else {
            exoPlayer.play()
            isPlaying.update { true }
        }

        saveCurrentPlaybackStatus()
        currentSong.value?.let { createNotification(it) }
    }

    fun next() {
        if (exoPlayer.mediaItemCount == 0 || songList.value.isEmpty()) return
        job?.cancel()

        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
            exoPlayer.play()
        } else {
            exoPlayer.seekTo(0)
            exoPlayer.play()
        }

        isPlaying.update { true }
        updateDurations()
        saveCurrentPlaybackStatus()
        currentSong.value?.let { createNotification(it) }
    }

    private fun updateDurations() {
        job = scope.launch {
            while (true) {
                currentDuration.update { exoPlayer.currentPosition.toFloat() }
                delay(1000)
            }
        }
    }

    fun seekTo(sliderPosition: Float) {
        val positionMs = sliderPosition.toLong().coerceIn(0L, exoPlayer.duration)
        exoPlayer.seekTo(positionMs)
        currentDuration.update { positionMs.toFloat() }
        if (!exoPlayer.isPlaying) {
            currentSong.value?.let { createNotification(it) }
        }
    }

    private fun play() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItems = songList.value.mapNotNull { song ->
            val path = song.path
            if (path.isNullOrEmpty()) {
                null
            } else {
                val file = File(path)
                if (!file.exists()) {
                    null
                } else {
                    val uri = Uri.fromFile(file)
                    MediaItem.Builder()
                        .setUri(uri)
                        .setTag(song.id)
                        .build()
                }
            }
        }

        if (mediaItems.isEmpty()) {
            return
        }

        val startIndex = songList.value.indexOfFirst { it.id == currentSong.value?.id }
            .takeIf { it >= 0 } ?: 0

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()

        isPlaying.update { true }
        exoPlayer.play()

        currentSong.value?.let { createNotification(it) }
        updateDurations()
    }

    private fun start() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItems = songList.value.mapNotNull { song ->
            val path = song.path
            if (path.isNullOrEmpty()) {
                null
            } else {
                val file = File(path)
                if (!file.exists()) {
                    null
                } else {
                    val uri = Uri.fromFile(file)
                    MediaItem.Builder()
                        .setUri(uri)
                        .setTag(song.id)
                        .build()
                }
            }
        }

        if (mediaItems.isEmpty()) {
            return
        }

        val startIndex = songList.value.indexOfFirst { it.id == currentSong.value?.id }
            .takeIf { it >= 0 } ?: 0

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()

        isPlaying.update { false }
        exoPlayer.pause()
        currentSong.value?.let { createNotification(it) }
        updateDurations()
    }

    fun setRepeatModeOption(option: Boolean) {
        when (option) {
            true -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            false -> exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        }
        currentRepeatOption.update { option }
    }

    fun setShuffleMode(status: Boolean) {
        exoPlayer.shuffleModeEnabled = status
        currentShuffleStatus.update { status }
    }

    private fun createNotification(song: Song) {
        val albumBitmap = if (song.albumArt.isNullOrEmpty()) {
            BitmapFactory.decodeResource(resources, R.drawable.album_art_default)
        } else {
            val albumUri = Uri.parse(song.albumArt)
            this.contentResolver.openInputStream(albumUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }

        val session = MediaSessionCompat(this, "music")

        val style = MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(session.sessionToken)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .addAction(R.drawable.ic_skip_previous, "prev", createPendingIntent(PREV))
            .addAction(
                if (isPlaying.value) R.drawable.ic_pause_circle else R.drawable.ic_play_circle,
                "play_pause",
                createPendingIntent(PLAY_PAUSE)
            )
            .addAction(R.drawable.ic_skip_next, "next", createPendingIntent(NEXT))
            .setSmallIcon(R.drawable.ic_mucsic_note)
            .setLargeIcon(albumBitmap)
            .setVibrate(null)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startForeground(1, notification)
            }
        } else {
            startForeground(1, notification)
        }
    }

    private fun createPendingIntent(type: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            action = type
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        isServiceRunning = false
    }
}