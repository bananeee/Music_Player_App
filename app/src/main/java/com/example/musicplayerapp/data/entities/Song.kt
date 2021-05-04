package com.example.musicplayerapp.data.entities

data class Song(
    var mediaId: String = "",
    var title: String = "",
    var songUrl: String = "",
    var artist: String = "",
    var bigCover: String = "",
    var favorite: Boolean = false
)