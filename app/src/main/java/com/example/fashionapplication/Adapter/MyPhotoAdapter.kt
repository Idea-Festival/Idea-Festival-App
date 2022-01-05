package com.example.fashionapplication.Adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.data.Post
import com.example.fashionapplication.data.PostDto

class MyPhotoAdapter(var context: Context?, var mPost: List<PostDto>): RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotoAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.posting_photo_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPhotoAdapter.ViewHolder, position: Int) {
        val post: PostDto = mPost.get(position)
        Glide.with(context!!).load(post.imageUrl).into(holder.postImage)

        holder.postImage.setOnClickListener {
            val editor: SharedPreferences.Editor = context!!.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("postid", post.uid)
            editor.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_screen, ProfileFragment()).commit()
        }
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