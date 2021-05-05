package com.example.musicplayerapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityLoginSignupBinding
import com.example.musicplayerapp.fragment.LoginFragment
import com.example.musicplayerapp.fragment.WelcomeFragment

class LoginSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
    }

//    public fun loadFragment(fragment: Fragment){
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.login_signup_fg_container, fragment)
//            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
//            .addToBackStack(null)
//            .commit()
//    }
}