package com.example.fashionapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto
import com.google.firebase.firestore.FirebaseFirestore

class MainFragmentRecyclerAdapter: RecyclerView.Adapter<MainFragmentRecyclerAdapter.CustomViewHolder>() {
    var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var postDto: ArrayList<PostDto> = arrayListOf()     // post를 담을 arraylisy
    var postUidList: ArrayList<String> = arrayListOf()  // 사용자의 uid를 담을 arraylist

    init {
        fireStore.collection("posts").orderBy("timestamp").addSnapshotListener { querySnapShot, error ->
            postDto.clear()
            postUidList.clear()
            for (snapshot in querySnapShot!!) {
                val item = snapshot.toObject(PostDto::class.java)
                postDto.add(item)
                postUidList.add(snapshot.id)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFragmentRecyclerAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_recyclerview_item, parent, false)
        return CustomViewHolder(view)
    }

    // view 매핑
    override fun onBindViewHolder(holder: MainFragmentRecyclerAdapter.CustomViewHolder, position: Int) {
        holder.postUserText.text = postDto[position].userId
        Glide.with(holder.itemView.context).load(postDto[position].imageUrl).centerCrop().into(holder.postMainImage)
        holder.postExplain.text = postDto[position].explain
        holder.postTimeStamp.text = postDto[position].timestamp
        holder.likeCount.text = postDto[position].favoriteCount.toString()
        holder.tag1.text = postDto[position].tag1.toString()
        holder.tag2.text = postDto[position].tag2.toString()
        holder.tag3.text = postDto[position].tag3.toString()
    }

    override fun getItemCount(): Int {
        return postDto.size
    }

    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postUserText = itemView.findViewById<TextView>(R.id.post_user_text)
        val postMainImage = itemView.findViewById<ImageView>(R.id.post_main_image)
        val postExplain = itemView.findViewById<TextView>(R.id.post_str)
        val postTimeStamp = itemView.findViewById<TextView>(R.id.post_timestamp)
        val likeCount = itemView.findViewById<TextView>(R.id.like_count)
        val tag1 = itemView.findViewById<TextView>(R.id.hasi1_text)
        val tag2 = itemView.findViewById<TextView>(R.id.hasi2_text)
        val tag3 = itemView.findViewById<TextView>(R.id.hasi3_text)
    }
}