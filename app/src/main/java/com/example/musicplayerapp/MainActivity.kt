package com.example.musicplayerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.musicplayerapp.Fragment.FavoriteListFragment
import com.example.musicplayerapp.Fragment.HomeFragment
import com.example.musicplayerapp.Fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private val bottomNavigation: BottomNavigationItemView = findViewById(R.id.bottomNavigation)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragment = HomeFragment()
        val profileFragment =
            ProfileFragment()
        val favoriteListFragment =
            FavoriteListFragment()

        loadFragment(mainFragment)

        bottomNavigation.setOnNavigationItemSelectedListener {
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