package com.example.musicplayerapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.activity.LoginSignupActivity
import com.example.musicplayerapp.activity.MainActivity
import com.example.musicplayerapp.databinding.FragmentLoginBinding
import com.example.musicplayerapp.databinding.FragmentPlayingBinding
import com.example.musicplayerapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

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
                val postListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val specEmail =
                            snapshot.child("users").child(binding.usernameLogin.text.toString())
                                .child("email").value.toString()

                        auth.signInWithEmailAndPassword(
                            specEmail,
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
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
                database.addValueEventListener(postListener)
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