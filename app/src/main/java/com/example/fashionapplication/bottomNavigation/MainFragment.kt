package com.example.fashionapplication.bottomNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapplication.R
import com.example.fashionapplication.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.view.*

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
//        val view = inflater.inflate(R.layout.fragment_main, container, false)


        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
}


/*
inner class MainFragmentRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var postDto: ArrayList<PostDto> = arrayListOf()
    private var userUid: ArrayList<String> = arrayListOf()
    init {
        fireStore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener{
                querySnapShot, firebaseFirestoreExeption ->
            postDto.clear()
            userUid.clear()
            for (snapshot in querySnapShot!!.documents) {
                val item = snapshot.toObject(PostDto::class.java)
                postDto.add(item!!)
                userUid.add(snapshot.id)
            }
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_recyclerview_item, parent, false)
        return CustomViewHolder(view)
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        // userid
        viewHolder.post_user_text.text = postDto[position].userId
        // image
        Glide.with(holder.itemView.context).load(postDto[position].imageUrl).into(viewHolder.post_main_img)
        // 설명글
        viewHolder.post_str.text = postDto[position].explain
        // likeCount
        viewHolder.like_count.text = postDto[position].favoriteCount.toString()
        // profile image
        Glide.with(holder.itemView.context).load(postDto[position].imageUrl).into(viewHolder.post_user)
        // tag
        viewHolder.hasi1.text = postDto[position].tag1
        viewHolder.hasi2.text = postDto[position].tag2
        viewHolder.hasi3.text = postDto[position].tag3
        // favorite
        viewHolder.like.setOnClickListener {
            favoriteEvent(position)
        }
        viewHolder.bookmark.setOnClickListener {
            if (postDto[position].bookmark.containsKey(uid)) {  // 클릭한 경우
                viewHolder.bookmark.setImageResource(R.drawable.tag_selected)
            } else {    // 클릭하지 않은 경우
                viewHolder.bookmark.setImageResource(R.drawable.tag_unselected)
            }
        }
        // comment
        viewHolder.comment.setOnClickListener {
            startActivity(Intent(activity, CommentActivity::class.java))
        }
        // favorite count, img change
        if (postDto[position].favorite.containsKey(uid)) {  // 클릭한 경우
            viewHolder.like.setImageResource(R.drawable.hanger_selected)
        } else {    // 클릭하지 않은 경우
            viewHolder.like.setImageResource(R.drawable.hanger_unselected)
        }
    }

    override fun getItemCount(): Int {
        return postDto.size
    }

    fun favoriteEvent(position: Int) {
        Log.d("tag","start favorite")
        var tsDoc = fireStore?.collection("images")?.document(userUid[position])
        fireStore?.runTransaction {
            val postDto = it.get(tsDoc!!).toObject(PostDto::class.java)

            // 좋아요가 눌린 경우
            if (postDto!!.favorite.containsKey(uid)) {
                postDto.favoriteCount = postDto.favoriteCount - 1
                postDto.favorite.remove(uid)
            } else {    // 눌리지 않은 경우
                postDto.favoriteCount = postDto.favoriteCount + 1
                postDto.favorite[uid!!] = true
            }
            // 트랜잭션 다시 서버로 돌려줌
            it.set(tsDoc, postDto)
        }
    }
}*/
