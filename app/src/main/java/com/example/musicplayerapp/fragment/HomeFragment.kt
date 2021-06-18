package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.ListAlbumAdapter
import com.example.musicplayerapp.adapter.ListAlbumAdapter.AlbumClickListener
import com.example.musicplayerapp.adapter.ListSongAdapter
import com.example.musicplayerapp.adapter.ListSongAdapter.SongClickListener
import com.example.musicplayerapp.data.utils.Status
import com.example.musicplayerapp.databinding.FragmentHomeBinding
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    @Inject
    lateinit var listAlbumAdapter: ListAlbumAdapter
    private lateinit var albumRecyclerView: RecyclerView

    //    private lateinit var listAlbumAdapter: ListAlbumAdapter
    private lateinit var albumClickListener: AlbumClickListener

    @Inject
    lateinit var listSongAdapter: ListSongAdapter
    private lateinit var songRecyclerView: RecyclerView

    //    private lateinit var listSongAdapter: ListSongAdapter
    private lateinit var songClickListener: SongClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

//        mainActivityViewModel.fetchAllSongs()

        setupAlbumRecyclerView()
        setupSongRecyclerView()

        binding.avatar.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.searchBar.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
//                    TODO("Not yet implemented")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
//                    TODO: Place text search function here
                    newText?.let { mainActivityViewModel.fetchSearchSongs(it) }
                    return false
                }
            }
        )

        binding.searchBar.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.listAlbums.visibility = View.GONE
                binding.textView.visibility = View.GONE
                binding.textView2.visibility = View.GONE
            } else {
                binding.listAlbums.visibility = View.VISIBLE
                binding.textView.visibility = View.VISIBLE
                binding.textView2.visibility = View.VISIBLE
            }

        }

        return binding.root
    }


    private fun setupAlbumRecyclerView() {
        albumClickListener = AlbumClickListener {
            mainActivityViewModel.fetchSongsFromAlbum(it.title)
        }
        listAlbumAdapter.albumClickListener = albumClickListener
//        listAlbumAdapter = ListAlbumAdapter()

        albumRecyclerView = binding.listAlbums
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        mainActivityViewModel.listAlbums.observe(viewLifecycleOwner, { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data.let { albums ->
                        listAlbumAdapter.submitList(albums)

                        Log.d("HomeFragment", "Number of albums: " + albums?.size)
                    }
                }
                Status.ERROR -> Log.d("HomeFragment", "Failed to retrieve albums")
                Status.LOADING -> Log.d("HomeFragment", "Retrieving albums...")
            }
        })
    }

    private fun setupSongRecyclerView() {
        songClickListener = SongClickListener { songClicked ->
            mainActivityViewModel.playOrToggleSong(songClicked)
            this.findNavController().navigate(R.id.action_homeFragment_to_playingFragment)
        }

        songClickListener.favoriteListener = { favoriteSong ->
            mainActivityViewModel.addFavoriteSong(favoriteSong.mediaId)
            listSongAdapter.notifyDataSetChanged()
        }
//        listSongAdapter = ListSongAdapter(songClickListener)

        listSongAdapter.songClickListener = songClickListener

        songRecyclerView = binding.listSongs
        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listSongAdapter
        }

        mainActivityViewModel.listSongs.observe(viewLifecycleOwner, { result ->
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

        mainActivityViewModel.isCurPlayingSongFavorited.observe(viewLifecycleOwner, {
            listSongAdapter.notifyDataSetChanged()
        })


    }

}