package com.example.musicplayerapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.media.extension.isPlaying
import com.example.musicplayerapp.media.extension.toSong
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private var curPlayingSong: Song? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        Log.d("MainActivity", "onCreate")
//        mainActivityViewModel.fetchAllSongs()

        updatePlayingBar()
        binding.playing.setOnClickListener { view ->
//            val playingFragment = PlayingFragment()
//            loadFragment(playingFragment)
            navController.navigate(R.id.globleActionToPlayingFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.playingFragment ||
                destination.id == R.id.resetPasswordFragment
            )
                hideBottomNavigationAndPlaying()
            else
                displayBottomNavigationAndPlaying()
        }
    }

    private fun updatePlayingBar() {
        mainActivityViewModel.curPlayingSong.observe(this) { mediaMetadata ->
            if (mediaMetadata == null) return@observe
            curPlayingSong = mediaMetadata.toSong()
            glide.load(mediaMetadata.description.iconUri).into(binding.songCover)
            binding.title.text = mediaMetadata.description.title
            binding.artist.text = mediaMetadata.description.subtitle
        }

        mainActivityViewModel.isCurPlayingSongFavorited.observe(this) {
            binding.favorite.setImageResource(
                if (it) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }

        mainActivityViewModel.playbackState.observe(this) {
            binding.play.setImageResource(
                if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
        }

        binding.play.setOnClickListener {
            curPlayingSong?.let {
                mainActivityViewModel.playOrToggleSong(it, true)
            }
        }

        binding.favorite.setOnClickListener {
            curPlayingSong?.let { song ->
                mainActivityViewModel.addFavoriteSong(song.mediaId)
//                it as ImageView
//                it.setImageResource(
//                    if (song.favorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
//                )
            }
        }
    }

    private fun hideBottomNavigationAndPlaying() {
        binding.bottomNavigation.visibility = View.GONE
        binding.playing.visibility = View.GONE
    }

    private fun displayBottomNavigationAndPlaying() {
        binding.bottomNavigation.visibility = View.VISIBLE
        if (mainActivityViewModel.curPlayingSong.value != null) {
            binding.playing.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }
}