package com.example.musicplayerapp.data.entities

import com.google.firebase.firestore.Exclude

data class User(
    val username: String? = null,
    val email: String? = null,
    val password: String? = "",
    @Exclude val favorite: List<String>
)
