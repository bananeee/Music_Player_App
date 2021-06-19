package com.example.musicplayerapp.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicplayerapp.data.utils.Constants.NETWORK_ERROR
import com.example.musicplayerapp.data.utils.Resource
import com.example.musicplayerapp.media.MusicServiceConnection.MediaBrowserConnectionCallback

/**
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance, typically a
 * [MusicService] or one of its subclasses.
 *
 * Typically it's best to construct/inject dependencies either using DI or, as UAMP does,
 * using [InjectorUtils] in the app module. There are a few difficulties for that here:
 * - [MediaBrowserCompat] is a final class, so mocking it directly is difficult.
 * - A [MediaBrowserConnectionCallback] is a parameter into the construction of
 *   a [MediaBrowserCompat], and provides callbacks to this class.
 * - [MediaBrowserCompat.ConnectionCallback.onConnected] is the best place to construct
 *   a [MediaControllerCompat] that will be used to control the [MediaSessionCompat].
 *
 *  Because of these reasons, rather than constructing additional classes, this is treated as
 *  a black box (which is why there's very little logic here).
 *
 *  This is also why the parameters to construct a [MusicServiceConnection] are simple
 *  parameters, rather than private properties. They're only required to build the
 *  [MediaBrowserConnectionCallback] and [MediaBrowserCompat] objects.
 */
class MusicServiceConnection(context: Context) {

    private val _isConnected = MutableLiveData<Resource<Boolean>>()
    val isConnected: LiveData<Resource<Boolean>>
        get() = _isConnected

    private val _networkError = MutableLiveData<Resource<Boolean>>()
    val networkError: LiveData<Resource<Boolean>>
        get() = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?>
        get() = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val curPlayingSong: LiveData<MediaMetadataCompat?>
        get() = _curPlayingSong

    lateinit var mediaController: MediaControllerCompat
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, MusicService::class.java),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    // Subscribe to specific mediaId to get access to media item from firebase
    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun release() {
        mediaBrowser.disconnect()
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            Log.d("MusicServiceConnection", "Connected")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                .apply {
                    registerCallback(MediaControllerCallback())
                }
            _isConnected.value = Resource.success(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "Suspended")
            _isConnected.value = Resource.error("The connection was suspended", false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "Failed")
            _isConnected.value = Resource.error("Couldn't connect to media browser", false)
        }
    }


    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            /**
             * When [ExoPlayer] stops we will receive a callback with "empty" metadata. This is a
             * metadata object which has been instantiated with default values. The default value
             * for media ID is null so we assume that if this value is null we are not playing
             * anything.
             */
            _curPlayingSong.value = metadata
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            /**
             * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
             * [MediaControllerCompat.Callback] (here). But since other connection status events
             * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
             * send it on to the other callback.
             */
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _networkError.value = Resource.error(
                    "Couldn't connect to server. Please check your internet connection.",
                    null
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

}