package com.example.musicplayerapp.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)