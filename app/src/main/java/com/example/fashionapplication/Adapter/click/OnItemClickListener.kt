package com.example.fashionapplication.Adapter.click

import android.view.View
import com.example.fashionapplication.data.PostDto

interface OnItemClickListener {
    fun onItemClick(view: View, data: PostDto, position: Int)
}