package com.blessingsoftware.blessingplay.core.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.blessingsoftware.blessingplay.core.data.local.AppDb
import com.blessingsoftware.blessingplay.core.data.local.SongEntity
import com.blessingsoftware.blessingplay.core.data.mapper.toSong
import com.blessingsoftware.blessingplay.core.data.mapper.toSongEntityForDelete
import com.blessingsoftware.blessingplay.core.data.mapper.toSongEntityForUpdate
import com.blessingsoftware.blessingplay.core.data.mapper.toSongRemovedEntity
import com.blessingsoftware.blessingplay.core.domain.model.Song
import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongRepositoryImpl(
    @ApplicationContext private val context: Context,
    appDb: AppDb
) : SongRepository {
    private val songDao = appDb.songDao
    private val songRemovedDao = appDb.songRemovedDao

    override suspend fun loadMediaFileAndSaveToDb(): Unit = withContext(Dispatchers.IO) {
        val songEntityList = getMediaFile();
        songDao.firstInsertSongEntities(songEntityList)
    }

    override suspend fun getAllSongs(): List<Song> {
        return songDao.getAllSongEntities().map { it.toSong() }
    }

    override suspend fun insertSongs() {
        val existingSongEntities = songDao.getAllSongEntities()
        val newSongEntitiesToInsert = getMediaFile().filter { newSongEntity ->
            existingSongEntities.none { it.fileId == newSongEntity.fileId }
        }
        if (newSongEntitiesToInsert.isNotEmpty()) {
            val existingSongRemovedEntities = songRemovedDao.getAllSongRemovedEntities()
            val filteredNewSongs = newSongEntitiesToInsert.filter { newSongEntity ->
                existingSongRemovedEntities.none { it.fileId == newSongEntity.fileId }
            }
            if (filteredNewSongs.isNotEmpty()) {
                songDao.insertSongEntities(filteredNewSongs)
            }
        }
    }

    override suspend fun updateSong(song: Song) {
        songDao.updateSongEntity(song.toSongEntityForUpdate())
    }

    override suspend fun deleteSong(song: Song) {
        songDao.deleteSongEntity(song.toSongEntityForDelete())
        songRemovedDao.upsertSongRemovedEntity(song.toSongRemovedEntity())
    }

    private fun getMediaFile(): List<SongEntity> {
        val songEntityList = mutableListOf<SongEntity>()

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
                val fileFormat = filePath.substringAfterLast('.', "unknown")

                val songEntity = SongEntity(
                    fileId = it.getLong(idColumn),
                    title = it.getString(titleColumn),
                    fileName = filePath.substringAfterLast('/'),
                    path = filePath,
                    albumArt = albumArt,
                    artistId = it.getInt(artistIdColumn),
                    artist = it.getString(artistColumn),
                    format = fileFormat,
                    mimeType = it.getString(mimeTypeColumn),
                    size = it.getLong(sizeColumn),
                    duration = it.getLong(durationColumn),
                    dateModified = it.getLong(dateModifiedColumn)
                )
                songEntityList.add(songEntity)
            }
        }
        cursor?.close()

        return songEntityList
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