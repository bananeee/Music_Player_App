package com.example.musicplayerapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.fragment.PlayingFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigation.setupWithNavController(navController)

//        val navHostFragment = binding.fragment

        binding.playing.setOnClickListener { view ->
//            val playingFragment = PlayingFragment()
//            loadFragment(playingFragment)
            navController.navigate(R.id.globleActionToPlayingFragment)
        }

        navController.addOnDestinationChangedListener{ _, destination, _ ->
           if (destination.id == R.id.playingFragment ||
                   destination.id == R.id.resetPasswordFragment)
               hideBottomNavigationAndPlaying()
            else
               displayBottomNavigationAndPlaying()
        }
    }

    private fun hideBottomNavigationAndPlaying(){
        binding.bottomNavigation.visibility = View.GONE
        binding.playing.visibility = View.GONE
    }

    private fun displayBottomNavigationAndPlaying(){
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.playing.visibility = View.VISIBLE
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, fragment)
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .addToBackStack(null)
            .commit()
    }
}