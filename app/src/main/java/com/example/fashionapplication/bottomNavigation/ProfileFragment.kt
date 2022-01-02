package com.example.fashionapplication.bottomNavigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionapplication.Adapter.MyPhotoAdapter
import com.example.fashionapplication.R
import com.example.fashionapplication.data.Post
import com.example.fashionapplication.function.OptionsActivity
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import com.google.firebase.database.DataSnapshot
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    var firebaseuser: FirebaseUser? = null
    lateinit var profileid: String
    lateinit var recyclerView: RecyclerView
    var postList: ArrayList<Post> = arrayListOf()
    lateinit var adapter: MyPhotoAdapter

    lateinit var username:TextView
    lateinit var profileImg: CircleImageView
    lateinit var option: ImageView
    lateinit var follow: Button
    lateinit var postCount: TextView
    lateinit var follower: TextView
    lateinit var fashionCount: TextView
    lateinit var addPost: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_profile, container, false)

        firebaseuser = FirebaseAuth.getInstance().currentUser
        val prefs: SharedPreferences = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("profileid", "none").toString()

        username = view.findViewById(R.id.user_name)
        profileImg = view.findViewById(R.id.main_img)
        option = view.findViewById(R.id.profile_setting)
        follow = view.findViewById(R.id.profile_follow_btn)
        postCount = view.findViewById(R.id.post_count)
        follower = view.findViewById(R.id.follower_count)
        fashionCount = view.findViewById(R.id.fashion_count)
        addPost = view.findViewById(R.id.add_post)

        recyclerView = view.findViewById(R.id.posting_recyclerview)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = linearLayoutManager
        adapter = MyPhotoAdapter(context, postList)
        recyclerView.adapter = adapter

        userInfo()
        getFollowers()
        getNrPosts()
        myPhotos()

        addPost.setOnClickListener {
            startActivity(Intent(context, writingPostActivity::class.java))
        }

        option.setOnClickListener {
            startActivity(Intent(context, OptionsActivity::class.java))
        }

        if (profileid == firebaseuser?.uid) {
            follow.visibility = View.GONE
        } else {
            checkFollow()
        }

        follow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val btn: String = follow.text.toString()
                if (btn.equals(follow)) {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(firebaseuser!!.uid).child("following").child(profileid)
                        .setValue(true)
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileid).child("followers").child(firebaseuser!!.uid)
                        .setValue(true)
                } else if (btn.equals("following")) {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(firebaseuser!!.uid).child("following").child(profileid).removeValue()
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(firebaseuser!!.uid).child("following").child(profileid).removeValue()
                }
            }
        })
        return view
    }

    // 포스팅 사진 정보 받아오기 메서드
    private fun myPhotos() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (dataSnapshot in snapshot.children) {
                    val post: Post? = dataSnapshot.getValue(Post::class.java)
                    if (post?.publisher.equals(profileid)) {
                        postList.add(post!!)
                    }
                }
                postList.reverse()       // 최신순 정렬
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null) {
                    return
                }
                val user = snapshot.getValue(com.example.fashionapplication.data.User::class.java)
                Glide.with(context!!).load(user?.imageurl).into(main_img)
                username.text = user?.username
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkFollow() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseuser!!.uid).child("following")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(profileid).exists()) {
                    follow.text = "following"
                } else {
                    follow.text = "follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

//    follower 숫자 얻기 메서드
    private fun getFollowers() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileid).child("followers")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                follower.text = ""+snapshot.childrenCount+"명"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 포스팅 갯수 세기 메서드
    private fun getNrPosts() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i: Int = 0
                for (dataSnapshot in snapshot.children) {
                    val post: Post? = dataSnapshot.getValue(Post::class.java)
                    if (post?.publisher.equals(profileid)) {
                        i++
                    }
                }
                post_count.text = "${i}개"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
