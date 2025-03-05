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
import com.blessingsoftware.blessingplay.core.presentation.utils.RepeatModeOption
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

    @Inject
    lateinit var appDb: AppDb

    private lateinit var exoPlayer: ExoPlayer

    private val songList = MutableStateFlow<List<Song>>(emptyList())
    private val currentSong = MutableStateFlow<Song?>(null)
    private val isPlaying = MutableStateFlow(false)

    private val maxDuration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)

    private val currentRepeatOption = MutableStateFlow<RepeatModeOption>(RepeatModeOption.OFF)

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
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        preferencesManager = PreferencesManager(applicationContext)
        loadPlaylistSettings()
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
                currentSong.update { songList.value.first() }
            } else {
                val song = appDb.songDao.getSongEntityBySongId(settings.lastSongId)
                currentSong.update { song.toSong() }
            }
            withContext(Dispatchers.Main) {
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
                    lastSongId = song.id
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
        job?.cancel()

        exoPlayer.stop()
        val index = songList.value.indexOf(currentSong.value)
        val prevIndex = if (index == 0) songList.value.size.minus(1) else index.minus(1)
        val prevItem = songList.value.get(prevIndex)

        currentSong.update { prevItem }
        val mediaItem = MediaItem.fromUri(Uri.parse(prevItem.path))
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.prepare()
        exoPlayer.play()
        isPlaying.update { true }

        currentSong.value?.let { createNotification(it) }
        updateDurations()
        saveCurrentPlaybackStatus()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
        isPlaying.update { exoPlayer.isPlaying }
        currentSong.value?.let { createNotification(it) }
        saveCurrentPlaybackStatus()
    }

    fun next() {
        job?.cancel()

        exoPlayer.stop()
        val index = songList.value.indexOf(currentSong.value)
        val nextIndex = index.plus(1).mod(songList.value.size)
        val nextItem = songList.value.get(nextIndex)

        currentSong.update { nextItem }
        val mediaItem = MediaItem.fromUri(Uri.parse(nextItem.path))
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.prepare()
        exoPlayer.play()
        isPlaying.update { true }

        currentSong.value?.let { createNotification(it) }
        updateDurations()
        saveCurrentPlaybackStatus()
    }

    private fun updateDurations() {
        job = scope.launch {
            maxDuration.update { currentSong.value?.duration?.toFloat() ?: 0f }
            while (true) {
                currentDuration.update { exoPlayer.currentPosition.toFloat() }
                delay(1000)
                if (exoPlayer.currentPosition.toFloat() >= maxDuration.value && maxDuration.value > 0f) {
                    next()
                    break
                }
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

        val mediaItems = songList.value.map { song ->
            MediaItem.fromUri(Uri.parse(song.path))
        }
        val startIndex = songList.value.indexOfFirst { it.id == currentSong.value?.id }
            .takeIf { it >= 0 } ?: 0

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()

        currentSong.value?.let { createNotification(it) }
        updateDurations()

        isPlaying.update { true }
        exoPlayer.play()
    }

    private fun start() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItems = songList.value.map { song ->
            MediaItem.fromUri(Uri.parse(song.path))
        }

        val startIndex = songList.value.indexOfFirst { it.id == currentSong.value?.id }
            .takeIf { it >= 0 } ?: 0

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()

        currentSong.value?.let { createNotification(it) }
        updateDurations()

        isPlaying.update { false }
        exoPlayer.pause()
    }

    fun setRepeatModeOption(option: RepeatModeOption) {
        when (option) {
            RepeatModeOption.ONE -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            RepeatModeOption.OFF -> exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        }
        currentRepeatOption.update { option }
    }

    private fun createNotification(song: Song) {
        val albumBitmap = if (song.albumArt.isNullOrEmpty()) {
            BitmapFactory.decodeResource(resources, R.drawable.vinyl)
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .addAction(R.drawable.ic_skip_previous, "prev", createPendingIntent(PREV))
            .addAction(
                if (isPlaying.value) R.drawable.ic_pause_circle else R.drawable.ic_play_circle,
                "play_pause",
                createPendingIntent(PLAY_PAUSE)
            )
            .addAction(R.drawable.ic_skip_next, "next", createPendingIntent(NEXT))
            .setSmallIcon(R.drawable.vinyl)
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
}