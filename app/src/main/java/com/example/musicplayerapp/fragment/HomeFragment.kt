package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.SongClickListener
import com.example.musicplayerapp.databinding.FragmentHomeBinding
import com.example.musicplayerapp.model.Song
import com.example.musicplayerapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

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

        songClickListener = SongClickListener {
            Toast.makeText(activity, "this is item has image id: " + it.toString(), Toast.LENGTH_LONG).show()
            this.findNavController().navigate(R.id.action_homeFragment_to_playingFragment)
        }
        listSongAdapter = ListSongAdapter(songClickListener)
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

        binding.avatar.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        return binding.root
    }

}