package com.example.fashionapplication.bottomNavigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    inner class PostAdapter: RecyclerView.Adapter<PostAdapter.ViewHolder>() {
        lateinit var mContext: Context
        var mPost: ArrayList<Post> = arrayListOf()
        lateinit var firebaseUser: FirebaseUser

        // 아이템 파일과 연결
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
            val view: View =LayoutInflater.from(mContext).inflate(R.layout.post_recyclerview_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
            firebaseUser = FirebaseAuth.getInstance().currentUser!!
            val post: Post = mPost.get(position)

            Glide.with(mContext).load(post.postimage).into(holder.profileImg)

            if (post.description == "") {
                holder.explain.visibility = View.GONE
            } else {
                holder.explain.visibility = View.VISIBLE
                holder.explain.text = "${post.description}개"
            }

//            publisherInfo(holder.profileImg, holder.profileUser)
        }

        override fun getItemCount(): Int {
            return mPost.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var profileImg: ImageView
            lateinit var profileUser: TextView
            lateinit var mainPost: ImageView
            lateinit var like: ImageView
            lateinit var likeCount: TextView
            lateinit var star: ImageView
            lateinit var comment: ImageView
            lateinit var bookmark: ImageView
            lateinit var explain: TextView
            lateinit var tag1: TextView
            lateinit var tag2: TextView
            lateinit var tag3: TextView

            fun ViewHolder(itemView: View) {
                super.itemView
                profileImg = itemView.findViewById(R.id.post_user)
                profileUser = itemView.findViewById(R.id.post_user_text)
                mainPost = itemView.findViewById(R.id.post_main_img)
                like = itemView.findViewById(R.id.like)
                likeCount = itemView.findViewById(R.id.like_count)
                star = itemView.findViewById(R.id.star)
                comment = itemView.findViewById(R.id.comment)
                bookmark = itemView.findViewById(R.id.bookmark)
                explain = itemView.findViewById(R.id.post_str)
                tag1 = itemView.findViewById(R.id.hasi1)
                tag2 = itemView.findViewById(R.id.hasi2)
                tag3 = itemView.findViewById(R.id.hasi3)
            }
        }
    }
}
