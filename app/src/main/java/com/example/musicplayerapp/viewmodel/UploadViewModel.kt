package com.example.musicplayerapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.remote.MusicDatabase
import kotlinx.coroutines.launch

class UploadViewModel : ViewModel() {
    private val musicDatabase = MusicDatabase()
    private var _songUri = MutableLiveData<Uri>()
    val songUri: LiveData<Uri>
        get() = _songUri

    private var _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    fun setSongUri(uri: Uri) {
        _songUri.value = uri
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}