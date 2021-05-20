package com.example.musicplayerapp.data.remote

import android.util.Log
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.utils.Constants.ALBUM_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)
    private val albumCollection = firestore.collection(ALBUM_COLLECTION)

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
            Log.e("MusicDatabase", "Cannot get song list")
            emptyList<Album>()
        }
    }

//    suspend fun getSearchSongs(query: String): List<Song> {
//        return try {
//            songCollection.whereArrayContains("title", query).get().await().toObjects(Song::class.java)
//        } catch (e: Exception) {
//            Log.e("MusicDatabase", "Cannot find this song")
//            emptyList<Song>()
//        }
//    }

}