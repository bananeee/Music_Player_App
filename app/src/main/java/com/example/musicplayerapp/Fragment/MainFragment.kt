package com.example.musicplayerapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.Adapter.ListAlbumAdapter
import com.example.musicplayerapp.Entity.Album
import com.example.musicplayerapp.R

class MainFragment : Fragment() {
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var listAlbumAdapter: ListAlbumAdapter
    private var listAlbum: ArrayList<Album> = ArrayList()

    val childFragment = ListFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        listAlbumAdapter =
            ListAlbumAdapter(listAlbum)
        albumRecyclerView = view.findViewById(R.id.listAlbum)
        albumRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = listAlbumAdapter
        }

        addChildFragment(childFragment,
            R.id.listSongFragment
        )

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (j: Int in 1..10){
            listAlbum.add(
                Album(
                    R.drawable.blue_neighbourhood,
                    "Wind",
                    "Troye Sivan"
                )
            )
        }
    }

    fun Fragment.addChildFragment(fragment: Fragment, frameId: Int){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(frameId, fragment).commit()
    }
}