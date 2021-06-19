package com.example.musicplayerapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.example.musicplayerapp.media.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    @Named("ViewModelMusicDatabase") private val musicDatabase: MusicDatabase):  ViewModel() {
    // TODO: Implement the ViewModel

    private var _albumCover = MutableLiveData<String>()
    val albumCover: LiveData<String>
        get() = _albumCover

    private var _albumTitle = MutableLiveData<String>()
    val albumTitle: LiveData<String>
        get() = _albumTitle


    fun fetchAlbumDetail(albumTitle: String) {
        viewModelScope.launch {
            val album = musicDatabase.getAlbumDetail(albumTitle)
            _albumCover.value = album.albumCover
            _albumTitle.value = album.title
        }
    }
}