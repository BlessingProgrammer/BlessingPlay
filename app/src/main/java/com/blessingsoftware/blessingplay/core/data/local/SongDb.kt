package com.blessingsoftware.blessingplay.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class, SongRemovedEntity::class],
    version = 1
)

abstract class SongDb: RoomDatabase() {
    abstract val songDao: SongDao
    abstract val songRemovedDao: SongRemovedDao
}