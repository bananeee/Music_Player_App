package com.example.musicplayerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.LoginSignupActivity
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)

        binding.btnLogin.setOnClickListener {view ->
           view.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }

        return binding.root
    }

}