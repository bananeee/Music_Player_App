package com.example.musicplayerapp.audio

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.MutableLiveData

class MusicServiceConnection(context: Context, serviceComponent: ComponentName) {
    val _isConnectd = MutableLiveData<Boolean>()
        .apply { postValue(false) }
    val _networkFailure = MutableLiveData<Boolean>()
        .apply { postValue(false) }


}