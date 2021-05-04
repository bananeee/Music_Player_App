package com.example.musicplayerapp.media.callback

import android.widget.Toast
import com.example.musicplayerapp.media.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady) {
            // If playback is paused we remove the foreground state which allows the
            // notification to be dismissed. An alternative would be to provide a
            // "close" button in the notification which stops playback and clears
            // the notification.
            musicService.apply {
                stopForeground(false)
                isForegroundService = false
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(
            musicService,
            "An error occurred, make MusicPlayerEventListener more detail to see why",
            Toast.LENGTH_LONG
        ).show()
    }
}