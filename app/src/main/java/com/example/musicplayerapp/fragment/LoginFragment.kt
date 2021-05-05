package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentLoginBinding
import com.example.musicplayerapp.databinding.FragmentPlayingBinding

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }
}