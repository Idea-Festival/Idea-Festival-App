package com.example.fashionapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.data.Post

class MyPhotoAdapter(var context: Context?, var mPost: List<Post>): RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotoAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.posting_photo_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPhotoAdapter.ViewHolder, position: Int) {
        val post: Post = mPost.get(position)
        Glide.with(context!!).load(post.postimage).into(holder.postImage)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var postImage: ImageView

        fun ViewHolder(itemView: View) {
            postImage = itemView.findViewById(R.id.post_image)
        }
    }
}