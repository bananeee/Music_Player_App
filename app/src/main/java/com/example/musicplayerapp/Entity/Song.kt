package com.example.musicplayerapp.Entity

data class Song (
    var imgId: Int,
    var songName: String,
    var singer: String,
    var isPlaying: Boolean,
    var isFavorite: Boolean
){
}