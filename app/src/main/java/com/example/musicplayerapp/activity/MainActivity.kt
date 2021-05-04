package com.example.musicplayerapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.fragment.FavoriteListFragment
import com.example.musicplayerapp.fragment.HomeFragment
import com.example.musicplayerapp.fragment.PlayingFragment
import com.example.musicplayerapp.fragment.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val favoriteListFragment = FavoriteListFragment()

        loadFragment(mainFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home -> {
                    loadFragment(mainFragment)
                    true
                }
                R.id.navigation_favoriteList -> {
                    loadFragment(favoriteListFragment)
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        val playingFragment = PlayingFragment()
        binding.playing.setOnClickListener {
            loadFragment(playingFragment)
        }

    }

    public fun loadFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, fragment)
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .addToBackStack(null)
            .commit()
    }

    public fun hideBottomNavigationAndPlaying(){
        binding.bottomNavigation.visibility = View.GONE
        binding.playing.visibility = View.GONE
    }

    public fun displayBottomNavigationAndPlaying(){
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.playing.visibility = View.VISIBLE
    }

    public fun highlightHomeIcon(){
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }
}