package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blessingsoftware.blessingplay.core.data.local.dao.PlaylistDao
import com.blessingsoftware.blessingplay.core.data.local.dao.PlaylistSongCrossRefDao
import com.blessingsoftware.blessingplay.core.data.local.dao.SongDao
import com.blessingsoftware.blessingplay.core.data.local.dao.SongRemovedDao
import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.PlaylistSongCrossRefEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.SongEntity
import com.blessingsoftware.blessingplay.core.data.local.entities.SongRemovedEntity

@Database(
    entities = [
        SongEntity::class,
        SongRemovedEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRefEntity::class
    ],
    version = 1
)

abstract class AppDb : RoomDatabase() {
    abstract val songDao: SongDao
    abstract val songRemovedDao: SongRemovedDao
    abstract val playlistDao: PlaylistDao
    abstract val playlistSongCrossRefDao: PlaylistSongCrossRefDao
}