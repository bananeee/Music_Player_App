package com.example.musicplayerapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.example.musicplayerapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

//@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    //    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    //    @Inject
    private lateinit var musicDatabase: MusicDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
//        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        musicDatabase = MusicDatabase()
        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnForgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        return binding.root
    }

    private fun login() {
        when {
            TextUtils.isEmpty(binding.usernameLogin.text.toString()) -> {
                Toast.makeText(requireContext(), "Please enter username", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.passwordLogin.text.toString()) -> {
                Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_LONG).show()
            }
            else -> {
                musicDatabase.getUser(binding.usernameLogin.text.toString()) { user ->
                    auth.signInWithEmailAndPassword(
                        user.email,
                        binding.passwordLogin.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            updateUI(null)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please verify your email address",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Login failed, Please try again!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}