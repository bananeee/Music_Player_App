package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.model.Song
import com.example.musicplayerapp.R

class ListFragment : Fragment() {
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter
    private var listSong: ArrayList<Song> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_list, container, false)

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