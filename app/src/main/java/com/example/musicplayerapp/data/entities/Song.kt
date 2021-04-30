package com.example.musicplayerapp.data.entities

data class Song(
    var bigCover: String = "",
    var title: String = "",
    var artist: String = "",
    var mediaID: String = "",
    var songUrl: String = "",
    var favorite: Boolean = false
)