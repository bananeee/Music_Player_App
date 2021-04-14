package com.example.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.model.Album
import com.example.musicplayerapp.R
import com.example.musicplayerapp.model.Song

class ListAlbumAdapter : ListAdapter<Album, ListAlbumAdapter.AlbumViewHolder>(DiffCallback){
    class AlbumViewHolder(view: View) :  RecyclerView.ViewHolder(view){
        var albumImg : ImageView = view.findViewById(R.id.albumImage)
        var albumName : TextView = view.findViewById(R.id.albumName)
        var singer : TextView = view.findViewById(R.id.artist)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.equals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(
            view
        )
    }

    override fun onBindViewHolder(viewHolder: AlbumViewHolder, position: Int) {
        val currentAlbum = getItem(position)

        viewHolder.albumImg.setImageResource(currentAlbum.albumImg)
        viewHolder.albumName.text = currentAlbum.albumName
        viewHolder.singer.text = currentAlbum.singer
    }
}