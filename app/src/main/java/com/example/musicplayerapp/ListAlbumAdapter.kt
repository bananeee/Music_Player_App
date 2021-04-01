package com.example.musicplayerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAlbumAdapter(private val listAlbum : ArrayList<Album>) : RecyclerView.Adapter<ListAlbumAdapter.AlbumHolder>(){
    class AlbumHolder(view: View) :  RecyclerView.ViewHolder(view){
        var albumImg : ImageView = view.findViewById(R.id.albumImage)
        var albumName : TextView = view.findViewById(R.id.albumName)
        var singer : TextView = view.findViewById(R.id.artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumHolder(view)
    }

    override fun getItemCount(): Int {
        return listAlbum.size
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val currentAlbum = listAlbum[position]

        holder.albumImg.setImageResource(currentAlbum.albumImg)
        holder.albumName.text = currentAlbum.albumName
        holder.singer.text = currentAlbum.singer
    }
}