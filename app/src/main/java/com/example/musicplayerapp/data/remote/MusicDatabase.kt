package com.example.musicplayerapp.data.remote

import android.util.Log
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.utils.Constants.ALBUM_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.SONG_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.USER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.*

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)
    private val albumCollection = firestore.collection(ALBUM_COLLECTION)
    private val userCollection = firestore.collection(USER_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get song list")
            emptyList<Song>()
        }
    }

    suspend fun getAllAlbums(): List<Album> {
        return try {
            albumCollection.get().await().toObjects(Album::class.java)
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get album list, $e")
            emptyList<Album>()
        }
    }

    suspend fun getSongsFromAlbum(albumTitle: String): List<Song> {
        return try {
            val albumReference = albumCollection.document(albumTitle.toLowerCase(Locale.getDefault()))
            songCollection.whereEqualTo("album", albumReference).get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get any songs from album $albumTitle, $e")
            emptyList<Song>()
        }

    }

//    suspend fun getAllSongRealtime():

//    suspend fun getFavoriteSong(username: String): List<Song> {
//        userCollection.whereEqualTo("username", username).get().addOnSuccessListener {
//            Log.d
//        }
//    }

//    suspend fun setFavoriteSong(userId: String, ) {
//
//    }

//    suspend fun getSearchSongs(query: String): List<Song> {
//        return try {
//            songCollection.whereArrayContains("title", query).get().await().toObjects(Song::class.java)
//        } catch (e: Exception) {
//            Log.e("MusicDatabase", "Cannot find this song")
//            emptyList<Song>()
//        }
//    }

}