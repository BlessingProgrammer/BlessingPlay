package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SongEntity::class,
        SongRemovedEntity::class,
        PlaylistEntity::class
    ],
    version = 1
)

abstract class AppDb : RoomDatabase() {
    abstract val songDao: SongDao
    abstract val songRemovedDao: SongRemovedDao
    abstract val playlistDao: PlaylistDao
}