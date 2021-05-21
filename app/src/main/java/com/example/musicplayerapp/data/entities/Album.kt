package com.example.musicplayerapp.data.entities

import com.google.firebase.firestore.Exclude

data class Album(
    var albumCover: String = "",
    @Exclude var artis: String = "",
    var title: String = ""
)