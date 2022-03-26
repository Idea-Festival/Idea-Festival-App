package com.example.fashionapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto

class PostAdapter(private var postDtoList:List<PostDto>, private var uidList: ArrayList<String>, private var context: Context)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_recyclerview_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag1.text =  postDtoList.get(position).tag1
        holder.tag2.text =  postDtoList.get(position).tag2
        holder.tag3.text =  postDtoList.get(position).tag3
        holder.comment.text = postDtoList.get(position).explain

        context = holder.itemView.context
        val url: String? = postDtoList.get(position).imageUrl
        Glide.with(context).load(url).centerCrop().into(holder.uploadImg)
    }
    override fun getItemCount(): Int {
        return postDtoList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var uploadImg: ImageView
        lateinit var tag1: TextView
        lateinit var tag2: TextView
        lateinit var tag3: TextView
        lateinit var comment: TextView

        fun ViewHolder(itemView: View) {
            super.itemView

            uploadImg = itemView.findViewById(R.id.post_main_img)
            tag1 = itemView.findViewWithTag(R.id.hasi1)
            tag2 = itemView.findViewWithTag(R.id.hasi2)
            tag3 = itemView.findViewWithTag(R.id.hasi3)
            comment = itemView.findViewById(R.id.post_str)
        }
    }
}