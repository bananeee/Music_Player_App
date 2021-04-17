package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentPlayingBinding

class PlayingFragment : Fragment() {
    private lateinit var binding : FragmentPlayingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlayingBinding.inflate(layoutInflater)

        //Hide bottom navigation and playing container
        (activity as MainActivity?)!!.hideBottomNavigationAndPlaying()

        val homeFragment = HomeFragment()
        binding.back.setOnClickListener {
            (activity as MainActivity?)!!.loadFragment(homeFragment)
            (activity as MainActivity?)!!.highlightHomeIcon()
        }

        return binding.root
    }

}