package com.blessingsoftware.blessingplay.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.data.local.SongDb
import com.blessingsoftware.blessingplay.core.data.repository.SongRepositoryImpl
import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.DeleteSong
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.GetAllSongs
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.InsertSongs
import com.blessingsoftware.blessingplay.home.screens.library.song_list.domain.use_case.UpdateSong
import com.blessingsoftware.blessingplay.splash.domain.use_case.LoadMediaFileAndSaveToDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSongDb(application: Application): SongDb {
        return Room.databaseBuilder(
            application,
            SongDb::class.java,
            application.getString(R.string.db_name)
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongRepository(
        @ApplicationContext context: Context,
        songDb: SongDb
    ): SongRepository {
        return SongRepositoryImpl(context, songDb)
    }

    @Provides
    @Singleton
    fun provideLoadMediaFileAndSaveToDbUseCase(
        songRepository: SongRepository
    ): LoadMediaFileAndSaveToDb {
        return LoadMediaFileAndSaveToDb(songRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllSongsUseCase(
        songRepository: SongRepository
    ): GetAllSongs {
        return GetAllSongs(songRepository)
    }

    @Provides
    @Singleton
    fun provideInsertSongsUseCase(
        songRepository: SongRepository
    ): InsertSongs {
        return InsertSongs(songRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSongUseCase(
        songRepository: SongRepository
    ): UpdateSong {
        return UpdateSong(songRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSongUseCase(
        songRepository: SongRepository
    ): DeleteSong {
        return DeleteSong(songRepository)
    }

}