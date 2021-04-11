package com.example.musicplayerapp.model

data class Song (
    var imgId: Int,
    var songName: String,
    var singer: String,
    var isPlaying: Boolean,
    var isFavorite: Boolean
){
}