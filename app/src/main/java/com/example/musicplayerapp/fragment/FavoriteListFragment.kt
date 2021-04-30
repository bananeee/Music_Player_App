package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.SongClickListener
import com.example.musicplayerapp.databinding.FragmentFavoriteListBinding
import com.example.musicplayerapp.data.entities.Song

class FavoriteListFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteListBinding

    private lateinit var favoriteSongRecyclerView: RecyclerView
    private lateinit var favoriteSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    private var favoriteSong: ArrayList<Song> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteListBinding.inflate(layoutInflater)

        //Display bottom navigation and playing container
        (activity as MainActivity?)!!.displayBottomNavigationAndPlaying()

        favoriteSongRecyclerView = binding.favoriteSong
        songClickListener = SongClickListener {
            val playingFragment = PlayingFragment()
            (activity as MainActivity?)!!.loadFragment(playingFragment)
        }
        favoriteSongAdapter = ListSongAdapter(songClickListener)
        favoriteSongAdapter.submitList(favoriteSong)
        favoriteSongRecyclerView.apply {
            setHasFixedSize(true)
            adapter = favoriteSongAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
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