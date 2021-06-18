package com.example.musicplayerapp.data.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.entities.User
import com.example.musicplayerapp.data.utils.Constants.ALBUM_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.SONG_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.USER_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.USER_SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.*

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)
    private val albumCollection = firestore.collection(ALBUM_COLLECTION)
    private val userCollection = firestore.collection(USER_COLLECTION)
    private val userSongCollection = firestore.collection(USER_SONG_COLLECTION)


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
            val albumReference =
                albumCollection.document(albumTitle.toLowerCase(Locale.getDefault()))
            songCollection.whereEqualTo("album", albumReference).get().await()
                .toObjects(Song::class.java)
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get any songs from album $albumTitle, $e")
            emptyList<Song>()
        }

    }

    suspend fun getFavoriteSongsId(username: String): List<String> {
        return try {
            val songDocs = userSongCollection.whereEqualTo("user", username).get().await().documents
            var songIdList = emptyList<String>()
            for (doc in songDocs) {
                songIdList = songIdList + listOf(doc.data!!["songId"].toString())
            }
            songIdList
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get any songs from user $username, $e")
            emptyList<String>()
        }
    }

    suspend fun getFavoriteSongs(username: String): List<Song> {
        return try {
            val songIds = getFavoriteSongsId(username)
            Log.d("MusicDatabase", "Number of favorite song: " + songIds.size)
            var songs = emptyList<Song>()
            for (id in songIds) {
                val song = songCollection.whereEqualTo("mediaId", id).get().await()
                    .toObjects(Song::class.java)
                songs = songs + song
            }
            songs
//            emptyList()
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get any songs from user $username, $e")
            emptyList()
        }
    }

    suspend fun setFavoriteSong(username: String, mediaId: String): Boolean {
        return try {
            val data = hashMapOf(
                "user" to username,
                "songId" to mediaId
            )

            val doc = userSongCollection.whereEqualTo("user", username)
                .whereEqualTo("songId", mediaId).get().await().documents
            if (doc.isEmpty()) {
                userSongCollection.add(data)
            } else {
                doc[0].reference.delete()
            }
            true
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot set favorite")
            false
        }


    }

    suspend fun getAllSongsFavorite(username: String): List<Song> {
        return try {
            val songs = songCollection.get().await().toObjects(Song::class.java)
            val favoriteSongs = getFavoriteSongsId(username)
            for (song in songs) {
                if (favoriteSongs.contains(song.mediaId))
                    song.favorite = true
            }
            songs
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get song list")
            emptyList<Song>()
        }
    }

    suspend fun isFavoriteSong(username: String, mediaId: String): Boolean {
        return try {
            val doc = userSongCollection.whereEqualTo("user", username)
                .whereEqualTo("songId", mediaId).get().await().documents
            return doc.isNotEmpty()
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot check if song is favorited")
            false
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

    fun writeNewUser(username: String, email: String, password: String) {
        val user = User(username, email, password)
        userCollection.document(username).set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(
                    "MusicDatabase",
                    "Write new user successfully!"
                )
            }
            .addOnFailureListener { e ->
                Log.w("MusicDatabase", "writeNewUser", e)
            }
    }

    fun getUser(username: String, myCallback: (User) -> Unit) {
        userCollection.document(username).get().addOnSuccessListener { DocumentSnapshot ->
            val user = DocumentSnapshot.toObject(User::class.java)
            if (user != null) {
                myCallback(user)
            }

        }.addOnFailureListener { e ->
            Log.w("MusicDatabase", "getUser", e)
        }
    }

    fun getUserByEmail(email: String, myCallback: (User) -> Unit) {
        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    Log.d("MusicDatabase", user.toString())
                    myCallback(user)
                }

            }
            .addOnFailureListener { e ->
                Log.w("MusicDatabase", "getUserByEmail", e)
            }
    }

    fun writeSong(song: Song, context: Context) {
        songCollection.add(song)
            .addOnSuccessListener {
                Toast.makeText(context, "Successfully Uploaded !!!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.w("MusicDatabase", "writeSong", e)
            }

    }

}