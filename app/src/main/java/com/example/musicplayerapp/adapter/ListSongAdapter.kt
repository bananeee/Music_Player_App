package com.example.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.model.Song
import com.example.musicplayerapp.R

class ListSongAdapter(val songClickListener: SongClickListener) :
    ListAdapter<Song, ListSongAdapter.SongViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.equals(newItem)
        }
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgId: ImageView = view.findViewById(R.id.image)
        val songName: TextView = view.findViewById(R.id.songName)
        val singer: TextView = view.findViewById(R.id.singer)
        val playing: ImageView = view.findViewById(R.id.isPlaying)
        val favorite: ImageView = view.findViewById(R.id.isFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SongViewHolder, position: Int) {
        val currentItem = getItem(position)

        viewHolder.itemView.setOnClickListener{songClickListener.onClick(currentItem)}

        viewHolder.imgId.setImageResource(currentItem.imgId)
        viewHolder.songName.text = currentItem.songName
        viewHolder.singer.text = currentItem.singer
        if (currentItem.isPlaying) {
            viewHolder.playing.setImageResource(R.drawable.ic_pause)
        } else {
            viewHolder.playing.setImageResource(R.drawable.ic_play_arrow)
        }

        if (currentItem.isFavorite) {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite)
        } else {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}

class SongClickListener(val clickListener: (songId: Int) -> Unit) {
    fun onClick(song: Song) = clickListener(song.imgId)
}
