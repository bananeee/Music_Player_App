package com.example.musicplayerapp.media

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.musicplayerapp.data.utils.Constants.MEDIA_ROOT_ID
import com.example.musicplayerapp.data.utils.Constants.NETWORK_ERROR
import com.example.musicplayerapp.media.callback.MusicPlaybackPreparer
import com.example.musicplayerapp.media.callback.MusicPlayerEventListener
import com.example.musicplayerapp.media.callback.MusicPlayerNotificationListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    var isForegroundService = false

    private var isPlayerInitialized = false

    private var curPlayingSong: MediaMetadataCompat? = null

    companion object {
        var curSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicService", "Init music service")

        serviceScope.launch {
            firebaseMusicSource.fetchMediaData()
        }

        // Build PendingIntent that can be use to launch UI, for ex in Notification - In Spotify clone it's activityIntent
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, FLAG_UPDATE_CURRENT)
            }

        // Create new MediaSession
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
        }

        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         *
         */
        sessionToken = mediaSession.sessionToken

        /**
         * The notification manager will use our player and media session to decide when to post
         * notifications. When notifications are posted or removed our listener will be called, this
         * allows us to promote the service to foreground (required so that we're not killed if
         * the main UI is not visible).
         */
        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(firebaseMusicSource) {
            curPlayingSong = it
            preparePlayer(firebaseMusicSource.songs, it, true)
        }

        // Set MediaSessionConnector to connect MediaSession to Player
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    /**
     * This is the code that causes app to stop playing when swiping the activity away from
     * recent. The choice to do this is app specific. Some apps stop playback, while others allow
     * playback to continue and allow users to stop it with the notification.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        /**
         * By stopping playback, the player will transition to [Player.STATE_IDLE] triggering
         * [Player.EventListener.onPlayerStateChanged] to be called. This will cause the
         * notification to be hidden and trigger
         * [PlayerNotificationManager.NotificationListener.onNotificationCancelled] to be called.
         * The service will then remove itself as a foreground service, and will call
         * [stopSelf].
         */
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
        Log.d("MusicService", "Destroy music service")
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItem]s to browse/play.
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        /**
         * By default, all known clients are permitted to search, but only tell unknown callers
         * about search if permitted by the [BrowseTree].
         */

        /**
         * Unknown caller. There are two main ways to handle this:
         * 1) Return a root without any content, which still allows the connecting client
         * to issue commands.
         * 2) Return `null`, which will cause the system to disconnect the app.
         */
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentMediaId]. See [BrowseTree] for more details on
     * how this is build/more details about the relationships.
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                Log.d("MusicService", "Calling onLoadChildren()")
                // If the media source is ready, the results will be set synchronously here.
                val resultSent = firebaseMusicSource.whenReady { successfullyInitialized ->
                    if (successfullyInitialized) {
                        result.sendResult(firebaseMusicSource.asMediaItems())
                        Log.d(
                            "MusicService",
                            "There are ${firebaseMusicSource.asMediaItems().size} media items"
                        )
                        /**
                         * In the video, he said that if it hasn't have these lines
                         * of code, it will play automatically, but it do not happen -_-
                         * TODO: Delete these lines of code if not necessary
                         */
                        if (!isPlayerInitialized && firebaseMusicSource.songs.isNotEmpty()) {
//                            preparePlayer(
//                                firebaseMusicSource.songs,
//                                firebaseMusicSource.songs[0],
//                                false
//                            )
//                            isPlayerInitialized = true
                            Log.d("MusicService", "It would be miracle if it got here")
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)

                        Log.d("MusicService", "@.@ Don't know why it got here")
                    }
                }

                // If the results are not ready, the service must "detach" the results before
                // the method returns. After the source is ready, the lambda above will run,
                // and the caller will be notified that the results are ready.
                //
                // See [MediaItemFragmentViewModel.subscriptionCallback] for how this is passed to the
                // UI/displayed in the [RecyclerView].
                if (!resultSent) {
                    result.detach()
                    Log.d("MusicService", "Result is not ready, waiting for a while")
                }
            }
        }
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
//        TODO: Change this to not show thing that above bottom navigation
        val curSongIndex = if (curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
//        TODO: Change this to not using this deprecated function
        exoPlayer.prepare(firebaseMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    /**
     * For more information about this class, check this video at 16:00
     * https://www.youtube.com/watch?v=jAZn-J1I8Eg&t=2018s&ab_channel=AndroidDevelopers
     *
     * Uhm, after a long time figuring out, this class seems like listen for changes to player's
     * state or timeline and update media session's state, metadata and queue accordingly. But why
     * media session and notification do not using metadata of current song? Who know ¯\_(•ᴗ•)_/¯
     */
    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return firebaseMusicSource.songs[windowIndex].description
        }
    }
}