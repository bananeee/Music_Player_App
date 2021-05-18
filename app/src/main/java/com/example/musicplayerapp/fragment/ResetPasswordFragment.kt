package com.example.musicplayerapp.fragment

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
import com.example.musicplayerapp.databinding.FragmentResetPasswordBinding
import com.example.musicplayerapp.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding.btnUpdatePass.setOnClickListener {

            updatePassword(it)
        }
        return binding.root
    }

    private fun updatePassword(view: View) {
        val currentPass = binding.etCurrentPass.text.toString()
        val confirmPass = binding.etConfirmNewPass.text.toString()
        val newPass = binding.etNewPass.text.toString()

        when {
            TextUtils.isEmpty(currentPass) -> {
                Toast.makeText(requireContext(), "Please enter current password", Toast.LENGTH_LONG)
                    .show()
            }
            TextUtils.isEmpty(confirmPass) -> {
                Toast.makeText(requireContext(), "Please enter confirm password", Toast.LENGTH_LONG)
                    .show()
            }
            TextUtils.isEmpty(newPass) -> {
                Toast.makeText(requireContext(), "Please enter new password", Toast.LENGTH_LONG)
                    .show()
            }
            !newPass.equals(confirmPass) -> {
                Toast.makeText(requireContext(), "Password mismatching.", Toast.LENGTH_LONG).show()
            }
            else -> {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider.getCredential(user.email, currentPass)
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Re-Authentication success.",
                                    Toast.LENGTH_LONG
                                ).show()
                                user?.updatePassword(newPass)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Password changed successfully",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            view.findNavController()
                                                .navigate(R.id.action_resetPasswordFragment_to_profileFragment)
                                        }
                                    }

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "User is not existed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "User is not existed.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}