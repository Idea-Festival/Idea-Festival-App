package com.example.fashionapplication.bottomNavigation

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
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.databinding.FragmentClosetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore

class ClosetFragment : Fragment() {
    private lateinit var binding: FragmentClosetBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentClosetBinding.inflate(inflater, container, false)

        val uid = auth.uid.toString()
        getUser(uid)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ClosetRecyclerViewAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(activity, 3)
        return binding.root
    }

    inner class ClosetRecyclerViewAdapter: RecyclerView.Adapter<ClosetRecyclerViewAdapter.ViewHolder>() {

        private var postDto = arrayListOf<PostDto>()
        private var tempList = arrayListOf<PostDto>()
        init {
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection("posts").get().addOnSuccessListener { result ->
                for (snapshot in result) {
                    postDto.add(snapshot.toObject(PostDto::class.java))
                }

                for (i in 0 until postDto.size) {
                    val bookmark = postDto[i].bookmark
                    if (bookmark[auth.uid.toString()] == true) {
                        tempList.add(postDto[i])
                        Log.d("SUCCESS", "recycler view temp list: $tempList")
                    } else {
                        Log.d("SUCCESS", "else: tlqkf")
                    }
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.closet_fragment_recycler_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val width = resources.displayMetrics.widthPixels / 3
            holder.imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context)
                .load(tempList[position].imageUrl)
                .into(holder.imageView)
            holder.itemView.layoutParams.height = 400
            holder.itemView.requestLayout()
        }

        override fun getItemCount(): Int {
            return tempList.size
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val imageView = itemView.findViewById<ImageView>(R.id.closet_fragment_image)
        }
    }

    private fun getUser(name: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(name).child("username")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.getValue(String::class.java)
                    binding.userClosetText.text = "$username's closet"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        FirebaseDatabase.getInstance().reference.child("Users").child(name).child("imageurl")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userImage = snapshot.getValue(String::class.java)
                    Glide.with(context!!)
                        .load(userImage)
                        .into(binding.imageView2)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}