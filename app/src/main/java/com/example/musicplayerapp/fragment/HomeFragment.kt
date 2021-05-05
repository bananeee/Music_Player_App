package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.SongClickListener
import com.example.musicplayerapp.data.utils.Status
import com.example.musicplayerapp.databinding.FragmentHomeBinding
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var listSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)


        setupAlbumRecyclerView()
        setupSongRecyclerView()

        binding.avatar.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        return binding.root
    }


    private fun setupAlbumRecyclerView() {
        listAlbumAdapter = ListAlbumAdapter()

        albumRecyclerView = binding.listAlbums
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        mainActivityViewModel.listAlbums.observe(viewLifecycleOwner, Observer { albums ->
            albums?.let {
                listAlbumAdapter.submitList(mainActivityViewModel.listAlbums.value)
            }
        })
    }

    private fun setupSongRecyclerView() {
        songClickListener = SongClickListener {
            mainActivityViewModel.playOrToggleSong(it)
            this.findNavController().navigate(R.id.action_homeFragment_to_playingFragment)
        }
        listSongAdapter = ListSongAdapter(songClickListener)

        songRecyclerView = binding.listSongs
        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listSongAdapter
        }

        mainActivityViewModel.listSongs.observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data.let { songs ->
                        listSongAdapter.submitList(songs)

                        Log.d("HomeFragment", "Number of songs: " + songs?.size)
                    }
                }
                Status.ERROR -> Log.d("HomeFragment", "Failed to retrieve songs")
                Status.LOADING -> Log.d("HomeFragment", "Retrieving songs...")
            }
        })


    }

}