package com.example.musicplayerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User

class ProfileViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var musicDatabase: MusicDatabase = MusicDatabase()

    init {
        _email.value = auth.currentUser.email
        musicDatabase.getUserByEmail(auth.currentUser.email) { user ->
            _username.value = user.username
        }

    }

}