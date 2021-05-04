package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.SongClickListener
import com.example.musicplayerapp.data.utils.Status
import com.example.musicplayerapp.databinding.FragmentHomeBinding
import com.example.musicplayerapp.viewmodel.HomeViewModel
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
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        //Display bottom navigation and playing container
        (activity as MainActivity?)!!.displayBottomNavigationAndPlaying()

//        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        setupAlbumRecyclerView()
        setupSongRecyclerView()

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
//            Toast.makeText(activity, "this is item has image id: " + it.toString(), Toast.LENGTH_LONG).show()
            mainActivityViewModel.playOrToggleSong(it)
            val playingFragment = PlayingFragment()
            (activity as MainActivity?)!!.loadFragment(playingFragment)
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