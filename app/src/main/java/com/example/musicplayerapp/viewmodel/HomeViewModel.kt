package com.example.musicplayerapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.R
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.model.Song

class HomeViewModel() : ViewModel() {
    private var _listSong = MutableLiveData<ArrayList<Song>>()
    val listSong: LiveData<ArrayList<Song>>
        get() = _listSong

    private var _listAlbum = MutableLiveData<ArrayList<Album>>()
    val listAlbum: LiveData<ArrayList<Album>>
        get() = _listAlbum

    init {
        getSongs()
        getAlbums()
    }

    private fun getSongs() {
//        TODO(Update) Update me to display songs
        if (_listSong.value == null)
            _listSong.value = ArrayList()

        for (i: Int in 1..10){
            _listSong.value?.add(
                    Song(
                            R.drawable.unnamed,
                            "Wind",
                            "Troye Sivan",
                            false,
                            false
                    )
            )
        }
    }

    private fun getAlbums() {
        if (_listAlbum.value == null)
            _listAlbum.value = ArrayList()
        for (j: Int in 1..10){
            _listAlbum.value?.add(
                    Album(
                            R.drawable.blue_neighbourhood,
                            "Wind",
                            "Troye Sivan"
                    )
            )
        }
    }
}