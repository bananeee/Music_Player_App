package com.example.musicplayerapp.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.example.musicplayerapp.data.utils.Constants.MEDIA_ROOT_ID
import com.example.musicplayerapp.data.utils.Resource
import com.example.musicplayerapp.media.MusicServiceConnection
import com.example.musicplayerapp.media.extension.isPlayEnable
import com.example.musicplayerapp.media.extension.isPlaying
import com.example.musicplayerapp.media.extension.isPrepared
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    @Named("ViewModelMusicDatabase") private val musicDatabase: MusicDatabase
) : ViewModel() {

    private lateinit var allSongs: List<Song>

    private val _listSongs = MutableLiveData<Resource<List<Song>>>()
    val listSongs: LiveData<Resource<List<Song>>>
        get() = _listSongs

    private val _listFavoriteSongs = MutableLiveData<Resource<List<Song>>>()
    val listFavoriteSongs: LiveData<Resource<List<Song>>>
        get() = _listFavoriteSongs

    //    TODO: Change this when making album
    private var _listAlbums = MutableLiveData<Resource<List<Album>>>()
    val listAlbums: LiveData<Resource<List<Album>>>
        get() = _listAlbums

    private var _isCurPlayingSongFavorited = MutableLiveData<Boolean>()
    val isCurPlayingSongFavorited: LiveData<Boolean>
        get() = _isCurPlayingSongFavorited

    private var username: String = ""
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    //    TODO: Implement handling error
    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
//        TODO: Clear this when merge authenticate
//        username = "dai"
        _email.value = auth.currentUser.email
        musicDatabase.getUserByEmail(auth.currentUser.email) { user ->
            username = user.username.toString()
            Log.d("MainActivityViewModel", "name $username")
            fetchAllSongs()
        }

        fetchAllAlbums()
        fetchAllSongs()
//        fetchFavoriteSongs()
        subscribeToServiceDataSource()
    }

    private fun subscribeToServiceDataSource() {
        _listSongs.value = Resource.loading(null)
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
//                    val songs = children.map {
//                        Song(
//                            it.mediaId!!,
//                            it.description.title.toString(),
//                            it.description.mediaUri.toString(),
//                            it.description.subtitle.toString(),
//                            it.description.iconUri.toString()
//                        )
//                    }
//                    _listSongs.value = Resource.success(songs)
//                    Log.d("MainActivityViewModel", "Mapping songs from firebase to listSong")
                }
            })
    }

    fun fetchAllAlbums() {
        viewModelScope.launch {
            _listAlbums.value = Resource.success(musicDatabase.getAllAlbums())
        }
    }

    fun fetchSongsFromAlbum(albumTitle: String) {
        viewModelScope.launch {
            val songs = musicDatabase.getSongsFromAlbum(albumTitle)
            if (songs.isNotEmpty()) {
                _listSongs.value = Resource.success(songs)
            } else {
                _listSongs.value = Resource.error("List is empty or error occurs", emptyList())
            }
        }
    }

    fun fetchAllSongs() {
        viewModelScope.launch {
//            allSongs = musicDatabase.getAllSongs()
            allSongs = musicDatabase.getAllSongsFavorite(username)
            _listSongs.value = Resource.success(allSongs)

            val favoriteSongs = allSongs.filter { song -> song.favorite }
            _listFavoriteSongs.value = Resource.success(favoriteSongs)
        }
    }

    fun fetchAllSongsLocally() {
        _listSongs.value = Resource.success(allSongs)
    }

    fun fetchFavoriteSongs() {
        viewModelScope.launch {
//            val favoriteSongs = musicDatabase.getFavoriteSongs(username)
//            _listFavoriteSongs.value = Resource.success(favoriteSongs)

            val favoriteSongs = allSongs.filter { song -> song.favorite }
            _listFavoriteSongs.value = Resource.success(favoriteSongs)
        }
    }

    fun fetchSearchSongs(query: String) {
        val regex = query.split(" ").joinToString(
            transform = { "(?=.*$it)" },
            separator = ""
        ).toRegex(RegexOption.IGNORE_CASE)
        val songs = allSongs.filter {
            regex.containsMatchIn(it.title)
        }

        _listSongs.value = Resource.success(songs)
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    /**
     * This method takes a [MediaItemData] and does one of the following:
     * - If the item is *not* the active item, then play it directly.
     * - If the item *is* the active item, check whether "pause" is a permitted command. If it is,
     *   then pause playback, otherwise send "play" to resume playback.
     */
    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        viewModelScope.launch {
            val isFavorited = musicDatabase.isFavoriteSong(username, mediaItem.mediaId)
            _isCurPlayingSongFavorited.value = isFavorited
        }

        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (toggle) musicServiceConnection.transportControls.pause() else Unit
                    playbackState.isPlayEnable ->
                        musicServiceConnection.transportControls.play()
                    else ->
                        Log.e(
                            "MainActivityViewModel",
                            "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=${mediaItem.mediaId})"
                        )
                }
            }

        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})

//        TODO: This won't fix notification-service-never-destroy bug, but this bug only appears in api <= 25
        musicServiceConnection.release()
        Log.d("MainActivityViewModel", "Release")
    }

    fun addFavoriteSong(songId: String) {
        viewModelScope.launch {
            musicDatabase.setFavoriteSong(username, songId)
        }
//        TODO: Test
        val favoriteSongFromAllSongs = allSongs.find { it.mediaId == songId }
        if (favoriteSongFromAllSongs != null) {
            favoriteSongFromAllSongs.favorite = !favoriteSongFromAllSongs.favorite

            if (songId == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
                _isCurPlayingSongFavorited.value = favoriteSongFromAllSongs.favorite
            }
        }

//        _listSongs.value = Resource.success(allSongs)
        fetchFavoriteSongs()
    }

}