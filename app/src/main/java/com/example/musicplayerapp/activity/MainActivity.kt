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
import com.example.musicplayerapp.fragment.ProfileFragment

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

//        binding.playing.setOnClickListener {
//            val currentFragment: Fragment = getCurrentFragment(this)
//            var fragmentManager: FragmentManager = this.getSupportFragmentManager()
//            when(it.fragment){
//                fragmentManager.findFragmentById(R.id.homeFragment) ->
//            }
//
//        }
//        Log.i("MainActivity", "current fragment" + getCurrentFragment(this).toString())

    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, fragment)
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .addToBackStack(null)
            .commit()
    }

//    private fun getCurrentFragment(activity: MainActivity): Fragment {
//        var fragmentManager: FragmentManager = activity.getSupportFragmentManager()
//        val fragmentList: List<Fragment> = fragmentManager.getFragments()
//        if (fragmentList != null) {
//            for (i: Int in 0..fragmentList.size) {
//                val currentFragment : Fragment = fragmentList.get(i)
//                if (currentFragment != null && currentFragment.isVisible)
//                return currentFragment
//            }
//        }
//        return Fragment()
//    }
}