package com.example.fashionapplication.Adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapplication.data.PostDto

class MainFragmentRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var postDto: ArrayList<PostDto> = arrayListOf()     // post를 담을 arraylisy
    var postUidList: ArrayList<String> = arrayListOf()  // 사용자의 uid를 담을 arraylist

    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}