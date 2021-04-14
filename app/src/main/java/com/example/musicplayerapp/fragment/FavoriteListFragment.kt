package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.model.Song

class FavoriteListFragment : Fragment() {
    private lateinit var favoriteSongRecyclerView: RecyclerView
    private lateinit var favoriteSongAdapter: ListSongAdapter
    private var favoriteSong: ArrayList<Song> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_list, container, false)

        favoriteSongRecyclerView = view.findViewById(R.id.favoriteSong)
        favoriteSongAdapter = ListSongAdapter(favoriteSong)
        favoriteSongRecyclerView.apply {
            setHasFixedSize(true)
            adapter = favoriteSongAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (j: Int in 1..10) {
            favoriteSong.add(
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