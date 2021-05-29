package com.example.musicplayerapp.data.utils

import com.example.musicplayerapp.data.entities.User


object Constants {
    const val SONG_COLLECTION = "songs"

    const val ALBUM_COLLECTION = "album"

    const val USER_COLLECTION = "users"

    const val NOTIFICATION_CHANEL_ID = "music"

    const val NOTIFICATION_ID = 1

    const val MEDIA_ROOT_ID = "root_id"

    const val NETWORK_ERROR = "network error"

    const val UPDATE_PLAYER_POSITION_INTERVAL = 100L

    val user = User("dai", "dainguyenduc01@gmail.com", "123456")
}