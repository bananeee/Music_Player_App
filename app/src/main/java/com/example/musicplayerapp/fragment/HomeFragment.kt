package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.databinding.FragmentHomeBinding
import com.example.musicplayerapp.model.Song
import com.example.musicplayerapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        //Display bottom navigation and playing container
        (activity as MainActivity?)!!.displayBottomNavigationAndPlaying()

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        listAlbumAdapter = ListAlbumAdapter()

        viewModel.listAlbum.observe(viewLifecycleOwner, Observer { albums ->
            albums?.let {
                listAlbumAdapter.submitList(viewModel.listAlbum.value)
            }
        })
        albumRecyclerView = binding.listAlbum
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        listSongAdapter = ListSongAdapter()
        viewModel.listSong.observe(viewLifecycleOwner, Observer { songs ->
            songs?.let {
                listSongAdapter.submitList(songs)
            }
        })
        songRecyclerView = binding.listSong
        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listSongAdapter
        }

        return binding.root
    }

}