package com.example.musicplayerapp.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment: Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        binding.btnSubmit.setOnClickListener {
            reset(it)
        }

        return binding.root
    }

    private fun reset(view: View) {
        val email = binding.etForgotEmail.text.toString()
        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_LONG).show()
            }
            else -> {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Email sent successfully to reset your password",
                                Toast.LENGTH_LONG
                            ).show()

                            view.findNavController().navigateUp()
                            // view.findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                it.exception!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}