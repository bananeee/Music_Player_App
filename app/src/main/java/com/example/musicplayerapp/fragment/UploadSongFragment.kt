package com.example.musicplayerapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.media.MediaBrowserServiceCompat.RESULT_OK
import androidx.navigation.findNavController
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentUploadSongBinding
import java.util.jar.Manifest

class UploadSongFragment : Fragment(){
    private lateinit var binding: FragmentUploadSongBinding
    private val SONG_SUCCESS = 1
    private val IMAGE_SUCCESS = 2
    private val PERMISSION_CODE = 100

    private val testList = listOf("Album 1", "Album 2", "Album 3")
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadSongBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {view ->
            view.findNavController().navigateUp()
        }

        this.context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.album_array,
                android.R.layout.simple_spinner_item
                ).also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.albumDropDown.adapter = adapter
            }
        }

        binding.imageUpload.setOnClickListener {
            checkPermission()
        }

        binding.songUpload.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, SONG_SUCCESS)
        }



        return binding.root
    }

    private fun chooseImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == IMAGE_SUCCESS && resultCode == Activity.RESULT_OK){
            binding.imageUpload.setImageURI(data?.data)
            binding.plusIcon.visibility = View.GONE
        }
    }

    private fun checkPermission(){
        if(context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } == PackageManager.PERMISSION_DENIED){
            //Permission denied
            val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, PERMISSION_CODE)
        }
        else{
            chooseImage()
        }

    }
}