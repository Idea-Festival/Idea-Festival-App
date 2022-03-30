package com.example.fashionapplication.bottomNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MainFragment : Fragment() {
    private var fireStore: FirebaseFirestore? = null
    private var uid: String? = null
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fireStore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.postRecyclerview.adapter = MainPageFragmentRecyclerAdapter()
        binding.postRecyclerview.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    inner class MainPageFragmentRecyclerAdapter: RecyclerView.Adapter<MainPageFragmentRecyclerAdapter.CustomViewHolder>() {
        var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private val uid = FirebaseAuth.getInstance().uid

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainPageFragmentRecyclerAdapter.CustomViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_recyclerview_item, parent, false)
            return CustomViewHolder(view)
        }

        // view 매핑
        override fun onBindViewHolder(holder: MainPageFragmentRecyclerAdapter.CustomViewHolder, position: Int) {
            holder.postUserText.text = postDto[position].userId
            Glide.with(holder.itemView.context).load(postDto[position].imageUrl).centerCrop().into(holder.postMainImage)
            holder.postExplain.text = postDto[position].explain
            holder.postTimeStamp.text = postDto[position].timestamp
            holder.likeCount.text = postDto[position].favoriteCount.toString()
            Glide.with(holder.itemView.context).load(postDto[position].profileImg).centerCrop().into(holder.postUser)
            holder.tag1.text = postDto[position].tag1.toString()
            holder.tag2.text = postDto[position].tag2.toString()
            holder.tag3.text = postDto[position].tag3.toString()

            // like 버튼 이벤트
            holder.like.setOnClickListener {
                favoriteEvent(position)
            }

            // like count, like image 이벤트
            // like를 클릭한 경우
            if (postDto[position].favorite.containsKey(uid)) {
                holder.like.setImageResource(R.drawable.hanger_selected)
            } else {    // like를 클릭하지 않은 경우
                holder.like.setImageResource(R.drawable.hanger_unselected)
            }

            holder.postUser.setOnClickListener {
                val profileFragment = ProfileFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", postDto[position].uid)
                bundle.putString("userId", postDto[position].userId)
                profileFragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()?.
                replace(R.id.post_recyclerview, profileFragment)?.commit()
            }
        }

        override fun getItemCount(): Int {
            return postDto.size
        }

        inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val postUserText = itemView.findViewById<TextView>(R.id.post_user_text)
            val postMainImage = itemView.findViewById<ImageView>(R.id.post_main_image)
            val postExplain = itemView.findViewById<TextView>(R.id.post_str)
            val postTimeStamp = itemView.findViewById<TextView>(R.id.post_timestamp)
            val postUser = itemView.findViewById<CircleImageView>(R.id.post_user)
            val likeCount = itemView.findViewById<TextView>(R.id.like_count)
            val tag1 = itemView.findViewById<TextView>(R.id.hasi1_text)
            val tag2 = itemView.findViewById<TextView>(R.id.hasi2_text)
            val tag3 = itemView.findViewById<TextView>(R.id.hasi3_text)
            val like = itemView.findViewById<ImageView>(R.id.like)
        }

        fun favoriteEvent(position: Int) {
            val tsDoc = fireStore.collection("posts").document(postUidList[position])
            fireStore.runTransaction { transition ->
                val postDto = transition.get(tsDoc).toObject(PostDto::class.java)

                // 좋아요가 눌린 경우
                if (postDto!!.favorite.containsKey(uid)) {
                    postDto.favoriteCount -= 1
                    postDto.favorite.remove(uid)
                } else {    // 눌리지 않은 경우
                    postDto.favoriteCount += 1
                    postDto.favorite[uid!!] = true
                }

                transition.set(tsDoc, postDto)
            }
        }
    }
}
