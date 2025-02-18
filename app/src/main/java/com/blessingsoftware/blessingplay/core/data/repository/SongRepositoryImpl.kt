package com.blessingsoftware.blessingplay.core.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongRepositoryImpl(
    @ApplicationContext private val context: Context
) : SongRepository {
    private val songs: MutableList<Song> = mutableListOf()

    override suspend fun loadMediaFileAndSaveToMemory(): Unit = withContext(Dispatchers.IO) {
        val songList = mutableListOf<Song>()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_MODIFIED
        )

        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            while (it.moveToNext()) {
                val filePath = it.getString(pathColumn)
                val albumId = it.getLong(albumIdColumn)

                val albumArt = getAlbumArt(context, albumId)

                val song = Song(
                    id = it.position.toLong() + 1,
                    fileId = it.getLong(idColumn),
                    title = it.getString(titleColumn),
                    fileName = filePath.substringAfterLast('/'),
                    path = filePath,
                    albumArt = albumArt,
                    artistId = it.getInt(artistIdColumn),
                    artist = it.getString(artistColumn),
                    format = it.getString(mimeTypeColumn),
                    mimeType = it.getString(mimeTypeColumn),
                    size = it.getLong(sizeColumn),
                    duration = it.getLong(durationColumn),
                    dateModified = it.getLong(dateModifiedColumn)
                )
                songList.add(song)
            }
        }
        cursor?.close()

        songs.clear()
        songs.addAll(songList)
    }

    override suspend fun getAllSongs(): List<Song> = songs

    override suspend fun upsertSong(song: Song) {
        val index = songs.indexOfFirst { it.fileId == song.fileId }
        if (index != -1) {
            songs[index] = song
        } else {
            songs.add(song)
        }
    }

    override suspend fun updateSong(song: Song) {
        val index = songs.indexOfFirst { it.fileId == song.fileId }
        if (index != -1) {
            songs[index] = song
        }
    }

    override suspend fun deleteSong(song: Song) {
        songs.removeAll { it.fileId == song.fileId }
    }

    private fun getAlbumArt(context: Context, albumId: Long): String? {
        val albumUri = Uri.parse("content://media/external/audio/albumart")
        val albumArtUri = ContentUris.withAppendedId(albumUri, albumId)

        val inputStream = try {
            context.contentResolver.openInputStream(albumArtUri)
        } catch (e: Exception) {
            null
        }

        return if (inputStream != null) {
            inputStream.close()
            albumArtUri.toString()
        } else {
            null
        }
    }
}