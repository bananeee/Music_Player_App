package com.example.musicplayerapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.utils.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import com.example.musicplayerapp.media.MusicService
import com.example.musicplayerapp.media.MusicServiceConnection
import com.example.musicplayerapp.media.extension.currentPlaybackPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    val playbackState = musicServiceConnection.playbackState

    val curPlayingSong = musicServiceConnection.curPlayingSong
//    val curPlayingSong: LiveData<Song>
//        get() {
//            _curPlayingSong.value?.description.let {
//                val song = Song(
//                    it?.mediaId as String,
//                    it.title as String,
//                    it.mediaUri.toString(),
//                    it.subtitle as String,
//                    it.iconUri.toString()
//                )
//                song
//            }
//            return song
//        }

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long>
        get() = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>(0)
    val curPlayerPosition: LiveData<Long>
        get() = _curPlayerPosition


    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlaybackPosition ?: 0
//                Log.e("PlayingViewModel", "Position is ${playbackState.value?.position}")
                if (_curPlayerPosition.value != pos) {
                    _curPlayerPosition.value = pos
                    _curSongDuration.value = MusicService.curSongDuration
                }

                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }


}