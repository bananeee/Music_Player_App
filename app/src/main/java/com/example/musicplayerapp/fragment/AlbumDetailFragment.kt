package com.example.musicplayerapp.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.ListSongAdapter.SongClickListener
import com.example.musicplayerapp.data.utils.Status
import com.example.musicplayerapp.databinding.FragmentAlbumDetailBinding
import com.example.musicplayerapp.viewmodel.AlbumDetailViewModel
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var binding: FragmentAlbumDetailBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private val albumDetailViewModel: AlbumDetailViewModel by viewModels()

    private lateinit var albumSongRecyclerView: RecyclerView

    private val args: AlbumDetailFragmentArgs by navArgs()

    @Inject
    lateinit var albumSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumDetailBinding.inflate(layoutInflater)

//        albumSongRecyclerView = binding.albumSongs

        albumDetailViewModel.fetchAlbumDetail(args.albumId)
        setupSongRecyclerView()

        subcribeToObserver()

        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(AlbumDetailViewModel::class.java)
//        // TODO: Use the ViewModel
//
//
//    }

    private fun subcribeToObserver() {
        albumDetailViewModel.albumCover.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            glide.load(it).fitCenter().into(binding.albumCover)
        }

        albumDetailViewModel.albumTitle.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.title.text = it
        }
    }

    private fun setupSongRecyclerView() {
        songClickListener = SongClickListener{ songClicked ->
            mainActivityViewModel.playOrToggleSong(songClicked)
            this.findNavController().navigate(R.id.globleActionToPlayingFragment)
        }

        songClickListener.favoriteListener = { favoriteSong ->
            mainActivityViewModel.addFavoriteSong(favoriteSong.mediaId)
            mainActivityViewModel.fetchSongsFromAlbum(args.albumId)
        }

        albumSongAdapter.songClickListener = songClickListener

        albumSongRecyclerView = binding.albumSongs
        albumSongRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = albumSongAdapter
        }

        mainActivityViewModel.listAlbumSongs.observe(viewLifecycleOwner, { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data.let { songs ->
                        albumSongAdapter.submitList(songs)

                        Log.d("AlbumDetailFragment", "Number of songs: " + songs?.size)
                    }
                }
                Status.ERROR -> Log.d("AlbumDetailFragment", "Failed to retrieve songs")
                Status.LOADING -> Log.d("AlbumDetailFragment", "Retrieving songs...")
            }
        })


    }

}