package com.example.musicplayerapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.LoginSignupActivity
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding.logOutLabel.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginSignupActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.chagePassLabel.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_resetPasswordFragment)
        }
        return binding.root
    }
}