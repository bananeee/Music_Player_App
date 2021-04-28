package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentPlayingBinding

class PlayingFragment : Fragment() {
    private lateinit var binding : FragmentPlayingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlayingBinding.inflate(layoutInflater)

        val homeFragment = HomeFragment()
        binding.back.setOnClickListener {view ->
            view.findNavController().navigateUp()
        }

        return binding.root
    }

}