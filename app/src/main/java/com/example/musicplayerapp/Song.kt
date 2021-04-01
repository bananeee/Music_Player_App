package com.example.musicplayerapp

data class Song (
    var imgId: Int,
    var songName: String,
    var singer: String,
    var isPlaying: Boolean,
    var isFavorite: Boolean
){
}