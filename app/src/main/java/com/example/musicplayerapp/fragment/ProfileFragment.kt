package com.example.musicplayerapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.LoginSignupActivity
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.example.musicplayerapp.databinding.FragmentProfileBinding
import com.example.musicplayerapp.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        // Get the viewmodel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Set the viewmodel for databinding - this allows the bound layout access to all of the
        // data in the VieWModel
        binding.profileViewModel = viewModel

        auth = FirebaseAuth.getInstance()

        // Specify the current activity as the lifecycle owner of the binding. This is used so that
        // the binding can observe LiveData updates
        binding.setLifecycleOwner(this)

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