package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.model.Song

class HomeFragment : Fragment() {
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter
    private var listAlbum: ArrayList<Album> = ArrayList()

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter
    private var listSong: ArrayList<Song> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        listAlbumAdapter =
            ListAlbumAdapter(listAlbum)
        albumRecyclerView = view.findViewById(R.id.listAlbum)
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        listSongAdapter =
                ListSongAdapter(listSong)
        songRecyclerView = view.findViewById(R.id.listSong)
        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listSongAdapter
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (j: Int in 1..10){
            listAlbum.add(
                Album(
                    R.drawable.blue_neighbourhood,
                    "Wind",
                    "Troye Sivan"
                )
            )
        }
        for (i: Int in 1..10){
            listSong.add(
                    Song(
                            R.drawable.unnamed,
                            "Wind",
                            "Troye Sivan",
                            false,
                            false
                    )
            )
        }
    }

}