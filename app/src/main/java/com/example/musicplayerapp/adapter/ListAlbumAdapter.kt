package com.example.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.entities.Album
import com.example.musicplayerapp.data.entities.Song
import javax.inject.Inject

class ListAlbumAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<Album, ListAlbumAdapter.AlbumViewHolder>(DiffCallback) {

    lateinit var albumClickListener: AlbumClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AlbumViewHolder, position: Int) {
        val currentAlbum = getItem(position)

        viewHolder.itemView.setOnClickListener{ albumClickListener.onClick(currentAlbum) }
        glide.load(currentAlbum.albumCover).into(viewHolder.albumCover)
        viewHolder.title.text = currentAlbum.title
//        viewHolder.albumImg.setImageResource(currentAlbum.albumImg)
//        viewHolder.singer.text = currentAlbum.singer
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.equals(newItem)
        }
    }

    class AlbumClickListener(val clickListener: (album: Album) -> Unit) {
        fun onClick(album: Album) = clickListener(album)
    }

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var albumCover: ImageView = view.findViewById(R.id.albumImage)
        var title: TextView = view.findViewById(R.id.albumName)
//        var artist : TextView = view.findViewById(R.id.artist)
    }
}


