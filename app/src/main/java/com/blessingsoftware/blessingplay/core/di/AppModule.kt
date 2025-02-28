package com.blessingsoftware.blessingplay.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.core.data.local.AppDb
import com.blessingsoftware.blessingplay.core.data.repository.PlaylistRepositoryImpl
import com.blessingsoftware.blessingplay.core.data.repository.PlaylistSongCrossRefRepositoryImpl
import com.blessingsoftware.blessingplay.core.data.repository.SongRepositoryImpl
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistRepository
import com.blessingsoftware.blessingplay.core.domain.repository.PlaylistSongCrossRefRepository
import com.blessingsoftware.blessingplay.core.domain.repository.SongRepository
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.DeleteSong
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.InsertSongs
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.UpdateSong
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.DeletePlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.GetAllPlaylistsWithSongCount
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.UpsertPlaylist
import com.blessingsoftware.blessingplay.home.screens.playlist.domain.use_case.UpdatePlaylist
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.AddSongToPlaylist
import com.blessingsoftware.blessingplay.home.screens.song_list.domain.use_case.GetAllPlaylists
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.DeleteSongFromPlaylist
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.GetAllSongsFromPlaylist
import com.blessingsoftware.blessingplay.playlist_songs.domain.use_case.UpdateSongPosition
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
    fun provideSongDb(application: Application): AppDb {
        return Room.databaseBuilder(
            application,
            AppDb::class.java,
            application.getString(R.string.db_name)
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongRepository(
        @ApplicationContext context: Context,
        appDb: AppDb
    ): SongRepository {
        return SongRepositoryImpl(context, appDb)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(
        appDb: AppDb
    ): PlaylistRepository {
        return PlaylistRepositoryImpl(appDb)
    }

    @Provides
    @Singleton
    fun providePlaylistSongCrossRefRepository(
        appDb: AppDb
    ): PlaylistSongCrossRefRepository {
        return PlaylistSongCrossRefRepositoryImpl(appDb)
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

    @Provides
    @Singleton
    fun provideGetAllPlaylistsUseCase(
        playlistRepository: PlaylistRepository
    ): GetAllPlaylists {
        return GetAllPlaylists(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideAddSongToPlaylist(
        playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
    ): AddSongToPlaylist {
        return AddSongToPlaylist(playlistSongCrossRefRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllPlaylistsWithSongCountUseCase(
        playlistRepository: PlaylistRepository
    ): GetAllPlaylistsWithSongCount {
        return GetAllPlaylistsWithSongCount(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideInsertPlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): UpsertPlaylist {
        return UpsertPlaylist(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideUpdatePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): UpdatePlaylist {
        return UpdatePlaylist(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideDeletePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): DeletePlaylist {
        return DeletePlaylist(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllSongsFormPlaylistUseCase(
        playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
    ): GetAllSongsFromPlaylist {
        return GetAllSongsFromPlaylist(playlistSongCrossRefRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSongPositionUseCase(
        playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
    ): UpdateSongPosition {
        return UpdateSongPosition(playlistSongCrossRefRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSongFromPlaylistUseCase(
        playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
    ): DeleteSongFromPlaylist {
        return DeleteSongFromPlaylist(playlistSongCrossRefRepository)
    }
}