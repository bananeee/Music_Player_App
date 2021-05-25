package com.example.musicplayerapp.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayerapp.data.entities.Song
import com.example.musicplayerapp.data.remote.MusicDatabase
import com.example.musicplayerapp.data.utils.Constants.AUDIO
import com.example.musicplayerapp.data.utils.Constants.IMAGE
import com.example.musicplayerapp.databinding.FragmentUploadBinding
import com.example.musicplayerapp.viewmodel.ProfileViewModel
import com.example.musicplayerapp.viewmodel.UploadViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import java.util.*

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding

//    private lateinit var songUri: Uri
//    private lateinit var imageUri: Uri

    private lateinit var songsStorageReference: StorageReference

    private lateinit var imageStorageReference: StorageReference

    private lateinit var musicDatabase: MusicDatabase

    private lateinit var viewModel: UploadViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(layoutInflater)

        songsStorageReference = FirebaseStorage.getInstance().getReference("songs")

        imageStorageReference = FirebaseStorage.getInstance().getReference("img")

        musicDatabase = MusicDatabase()

        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)

        binding.btnAudio.setOnClickListener {
            selectAudio()
        }

        binding.btnImage.setOnClickListener {
            selectImage()
        }

        binding.btnUpload.setOnClickListener {
            upload()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when {
                requestCode == IMAGE -> {
//                    imageUri = data?.data!!
                    viewModel.setImageUri(data?.data!!)
                    var bitmap =
                        MediaStore.Images.Media.getBitmap(
                            activity?.contentResolver,
                            viewModel.imageUri.value
                        )
                    binding.btnImage.setImageBitmap(bitmap)
                }

                requestCode == AUDIO -> {
//                    songUri = data?.data!!
                    viewModel.setSongUri(data?.data!!)
//                    binding.uriTxt.text = songUri.toString()
                    binding.uriTxt.text = viewModel.songUri.value.toString()
                }
            }
        }
    }

    private fun selectAudio() {
        val intent = Intent()
        intent.setType("audio/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO)
    }

    private fun selectImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE)
    }

    private fun upload() {
        val songUri = viewModel.songUri.value
        val imageUri = viewModel.imageUri.value
        val songsRef = songsStorageReference.child(songUri?.lastPathSegment.toString())

        val imagesRef = imageStorageReference.child(imageUri?.lastPathSegment.toString())

        try {
            if (songUri != null) {

                var progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Uploading")
                progressDialog.show()

                songsRef.putFile(songUri).addOnProgressListener {
                    var progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    progressDialog.setMessage("Uploaded ${progress.toInt()}%")
                }.continueWithTask {
                    if (!it.isSuccessful) {
                        it.exception?.let {
                            throw it
                        }
                    }
                    songsRef.downloadUrl
                }.addOnCompleteListener {
//                    progressDialog.dismiss()
                    if (it.isSuccessful) {
                        val songDowloadUri = it.result
                        if (imageUri != null) {
                            imagesRef.putFile(imageUri).continueWithTask {
                                if (!it.isSuccessful) {
                                    it.exception?.let {
                                        throw it
                                    }
                                }
                                imagesRef.downloadUrl
                            }.addOnCompleteListener {
                                progressDialog.dismiss()
                                if (it.isSuccessful) {
                                    val imageDowloadUri = it.result
                                    val mediaId = UUID.randomUUID().toString()
                                    val title = binding.etTitle.text.toString()
                                    val artist = binding.etArtist.text.toString()

                                    val song = Song(
                                        mediaId,
                                        title,
                                        songDowloadUri.toString(),
                                        artist,
                                        imageDowloadUri.toString(),
                                        false
                                    )
                                    musicDatabase.writeSong(song, requireContext())
                                    Log.d("UploadFragment", songUri.toString())
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}