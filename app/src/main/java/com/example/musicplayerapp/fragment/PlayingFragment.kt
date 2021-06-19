package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.databinding.FragmentPlayingBinding
import com.example.musicplayerapp.media.extension.isPlaying
import com.example.musicplayerapp.media.extension.toSong
import com.example.musicplayerapp.viewmodel.MainActivityViewModel
import com.example.musicplayerapp.viewmodel.PlayingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_playing.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayingFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val playingViewModel: PlayingViewModel by viewModels()

    private lateinit var binding: FragmentPlayingBinding

    private var curPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null

    private var canUpdateSeekBar = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {view ->
            view.findNavController().navigateUp()
        }

        subscribeToObservers()

        setUpUIListener()

        return binding.root
    }

    private fun setUpUIListener() {
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

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.curTimePos.text = convertCurPlayerTimeToText(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                canUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainActivityViewModel.seekTo(it.progress.toLong())
                    canUpdateSeekBar = true
                }
            }
        })

        binding.playNext.setOnClickListener {
            mainActivityViewModel.skipToNextSong()
        }

        binding.playPrevious.setOnClickListener {
            mainActivityViewModel.skipToPrevious()
        }
    }

    private fun updateUI(mediaMetadata: MediaMetadataCompat) {
        binding.favorite.setImageResource(
            if (mainActivityViewModel.isCurPlayingSongFavorited.value == true)
                R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        binding.songName.text = mediaMetadata.description.title
        binding.singer.text = mediaMetadata.description.subtitle
        glide.load(mediaMetadata.description.iconUri).fitCenter().into(binding.songCover)
    }

    private fun subscribeToObservers() {
        playingViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            updateUI(it)
            curPlayingSong = it.toSong()
        }

        playingViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.play.setImageResource(
                if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
        }

        playingViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if (canUpdateSeekBar) {
//                if (it == null)
//                    Toast.makeText(context, "Oops", Toast.LENGTH_SHORT).show()
                binding.seekBar.progress = it.toInt()
                binding.curTimePos.text = convertCurPlayerTimeToText(it)
            }
//            Toast.makeText(context, "update $it", Toast.LENGTH_SHORT).show()
        }

        playingViewModel.curSongDuration.observe(viewLifecycleOwner) {
            binding.seekBar.max = it.toInt()
            binding.timeDur.text = convertCurPlayerTimeToText(it)
        }

        mainActivityViewModel.isCurPlayingSongFavorited.observe(viewLifecycleOwner) {
            binding.favorite.setImageResource(
                if (it) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }
    }

    private fun convertCurPlayerTimeToText(ms: Long): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(ms)
    }

}