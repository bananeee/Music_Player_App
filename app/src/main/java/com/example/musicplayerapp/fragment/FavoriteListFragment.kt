package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.ListSongAdapter.SongClickListener
import com.example.musicplayerapp.databinding.FragmentFavoriteListBinding
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.utils.Status
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteListBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var favoriteSongRecyclerView: RecyclerView

    @Inject
    lateinit var favoriteSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteListBinding.inflate(layoutInflater)

        favoriteSongRecyclerView = binding.favoriteSong

        setupSongRecyclerView()

        return binding.root
    }

    private fun setupSongRecyclerView() {
        songClickListener = SongClickListener{ songClicked ->
            mainActivityViewModel.playOrToggleSong(songClicked)
            this.findNavController().navigate(R.id.action_favoriteListFragment_to_playingFragment)
        }

        songClickListener.favoriteListener = { favoriteSong ->
            mainActivityViewModel.addFavoriteSong(favoriteSong.mediaId)
        }

        favoriteSongAdapter.songClickListener = songClickListener

        favoriteSongRecyclerView = binding.favoriteSong
        favoriteSongRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = favoriteSongAdapter
        }

        mainActivityViewModel.listFavoriteSongs.observe(viewLifecycleOwner, { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data.let { songs ->
                        favoriteSongAdapter.submitList(songs)

                        Log.d("HomeFragment", "Number of songs: " + songs?.size)
                    }
                }
                Status.ERROR -> Log.d("HomeFragment", "Failed to retrieve songs")
                Status.LOADING -> Log.d("HomeFragment", "Retrieving songs...")
            }
        })


    }

}