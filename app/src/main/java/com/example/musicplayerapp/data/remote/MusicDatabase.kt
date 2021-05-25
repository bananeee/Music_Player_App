package com.example.musicplayerapp.data.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.utils.Constants.SONG_COLLECTION
import com.example.musicplayerapp.data.utils.Constants.USER_COLLECTION
import com.example.musicplayerapp.model.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)
    private val userCollection = firestore.collection(USER_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            Log.e("MusicDatabase", "Cannot get song list")
            emptyList<Song>()
        }
    }

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