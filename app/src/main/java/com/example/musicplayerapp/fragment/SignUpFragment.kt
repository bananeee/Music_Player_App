package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentSignUpBinding
import com.example.musicplayerapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {
            signUp(it)
        }
        return binding.root
    }

    private fun signUp(view: View) {

        val mail = binding.mail.text.toString()
        val password = binding.passwordLogin.text.toString()
        val username = binding.usernameLogin.text.toString()
        val rePassword = binding.repassword.text.toString()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(username) -> {
                Toast.makeText(requireContext(), "Please enter username", Toast.LENGTH_LONG)
                    .show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_LONG)
                    .show()
            }
            rePassword != password -> {
                Toast.makeText(requireContext(), "Password is not matched", Toast.LENGTH_LONG)
                    .show()
            }
            username.length <= 2 -> {
                Toast.makeText(requireContext(), "Username at least 3 characters", Toast.LENGTH_LONG)
                    .show()
            }
            else -> {
                auth.createUserWithEmailAndPassword(
                    mail,
                    password
                ).addOnCompleteListener {
                    // If registration is successfully done
                    if (it.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    // Save a info user to realtime database
                                    writeNewUser(username, mail, password)
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration Success",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()

                                    view.findNavController()
                                        .navigate(R.id.action_signUpFragment_to_loginFragment)
                                }
                            }
                    } else {
                        // If the registering is not successful then show error message
                        Toast.makeText(
                            requireContext(),
                            it.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }
    }

    fun writeNewUser(username: String, email: String, password: String) {
        val user = User(username, email, password)
        database.child("users").child(username).setValue(user)
    }
}