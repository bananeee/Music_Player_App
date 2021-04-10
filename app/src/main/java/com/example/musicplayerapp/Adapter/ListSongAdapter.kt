package com.example.musicplayerapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.Entity.Song
import com.example.musicplayerapp.R

class ListSongAdapter(private val listSong: ArrayList<Song>):  RecyclerView.Adapter<ListSongAdapter.ListSongViewHolder>(){
    class ListSongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgId: ImageView = view.findViewById(R.id.image)
        val songName: TextView = view.findViewById(R.id.songName)
        val singer: TextView = view.findViewById(R.id.singer)
        val playing: ImageView = view.findViewById(R.id.isPlaying)
        val favorite: ImageView = view.findViewById(R.id.isFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ListSongViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    override fun onBindViewHolder(viewHolder: ListSongViewHolder, position: Int) {
        val currentItem = listSong[position]

        viewHolder.imgId.setImageResource(currentItem.imgId)
        viewHolder.songName.text = currentItem.songName
        viewHolder.singer.text = currentItem.singer
        if (currentItem.isPlaying){
            viewHolder.playing.setImageResource(R.drawable.ic_pause)
        }
        else{
            viewHolder.playing.setImageResource(R.drawable.ic_play_arrow)
        }

        if (currentItem.isFavorite){
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite)
        }else{
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

}