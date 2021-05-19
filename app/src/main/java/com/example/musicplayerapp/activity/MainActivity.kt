package com.example.musicplayerapp.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.media.extension.isPlaying
import com.example.musicplayerapp.media.extension.toSong
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private var curPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigation.setupWithNavController(navController)

//        val navHostFragment = binding.fragment

        updatePlayingBar()
        binding.playing.setOnClickListener { view ->
//            val playingFragment = PlayingFragment()
//            loadFragment(playingFragment)
            navController.navigate(R.id.globleActionToPlayingFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.playingFragment)
                hideBottomNavigationAndPlaying()
            else
                displayBottomNavigationAndPlaying()
        }

    }

    private fun updatePlayingBar() {
        mainActivityViewModel.curPlayingSong.observe(this) { mediaMetadata ->
            if (mediaMetadata == null) return@observe
                curPlayingSong = mediaMetadata.toSong()
                binding.title.text = mediaMetadata.description.title
                binding.artist.text = mediaMetadata.description.subtitle
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
}