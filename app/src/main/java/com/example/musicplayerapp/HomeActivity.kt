package com.example.musicplayerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter
    private var listSong: ArrayList<Song> = ArrayList()

    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter
    private var listAlbum: ArrayList<Album> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        for (j: Int in 1..10){
            listAlbum.add(Album(R.drawable.blue_neighbourhood, "Wind", "Troye Sivan"))
        }
        listAlbumAdapter = ListAlbumAdapter(listAlbum)
        albumRecyclerView = findViewById(R.id.listAlbum)
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        for (i: Int in 1..10){
            listSong.add(Song(R.drawable.unnamed, "Wind", "Troye Sivan", false, false))
        }
        listSongAdapter = ListSongAdapter(listSong)
        songRecyclerView = findViewById(R.id.listSong)
        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = listSongAdapter
        }

    }
}