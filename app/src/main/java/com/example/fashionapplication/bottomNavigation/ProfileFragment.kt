package com.example.fashionapplication.bottomNavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.post_recyclerview_item.view.*

class ProfileFragment : Fragment() {

    private var fragmentView: View? = null
    private lateinit var firestore: FirebaseFirestore
    var uid:String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var imageview: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_profile, container, false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fragmentView?.posting_recyclerview?.adapter = ProfileFragmentRecyclerAdapter()
        Log.d("tag","tlqk3")
        fragmentView?.posting_recyclerview?.layoutManager = GridLayoutManager(activity, 3)

        val addPost = fragmentView?.findViewById<ImageView>(R.id.add_post)
        addPost?.setOnClickListener {
            startActivity(Intent(activity, writingPostActivity::class.java))
        }
        return fragmentView!!
    }
    inner class ProfileFragmentRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var postDto: ArrayList<PostDto> = arrayListOf()
        init {
            // db의 값 읽어오기, uid 가 내 uid 일때만 검색
            firestore.collection("images").whereEqualTo("uid",uid).addSnapshotListener { querySnapShot, firebaseFirestoreExeption ->
                if (querySnapShot == null) return@addSnapshotListener

                // get data
                for (snapshot in querySnapShot.documents) {     // 원인
                    Log.d("tag","tlqkf")
                    val item = snapshot.toObject(PostDto::class.java)
                    postDto.add(item!!)
                }
                fragmentView?.post_count?.text = postDto.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            Log.d("tag","tlqkf1")
            val width = resources.displayMetrics.widthPixels / 3
            imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width*2)   // 이미지 사이즈 지정
            return ProfileViewHolder(imageview)
        }

        inner class ProfileViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            Log.d("tag","tlqkf2")
            val imageview = (holder as ProfileViewHolder).imageview
            Glide.with(holder.itemView.context).load(postDto[position]).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return postDto.size
        }
    }
}