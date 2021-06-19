package com.example.musicplayerapp.di

import com.example.musicplayerapp.data.remote.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    @Named("ViewModelMusicDatabase")
    fun provideMusicDatabase() = MusicDatabase()
}