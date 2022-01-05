package com.example.fashionapplication.bottomNavigation

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.data.User
import com.example.fashionapplication.function.OptionsActivity
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private var mySaves: ArrayList<String> = arrayListOf()
    private lateinit var firebaseUser: FirebaseUser
    private var profileid: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPhotoAdapter
    private lateinit var postList: ArrayList<PostDto>
    private lateinit var option: ImageView
    private lateinit var profileImg: CircleImageView
    private lateinit var addPost: ImageView
    private lateinit var follower: TextView
    private lateinit var username: TextView
    private lateinit var followBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pres: SharedPreferences = requireContext().getSharedPreferences("PRES", MODE_PRIVATE)
        profileid = pres.getString("profileid", "none")

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_profile, container, false)
        addPost = view.findViewById(R.id.add_post)
        option = view.findViewById(R.id.profile_setting)
        profileImg = view.findViewById(R.id.main_img)
        follower = view.findViewById(R.id.follower_count)
        followBtn = view.findViewById(R.id.profile_follow_btn)
        username = view.findViewById(R.id.user_name)

        recyclerView = view.findViewById(R.id.posting_recyclerview)
        recyclerView.setHasFixedSize(true)
        var layoutmanager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = layoutmanager
        postList = arrayListOf()
        adapter = MyPhotoAdapter(context, postList)
        recyclerView.adapter = adapter

        userInfo()
        user()

        if (profileid.equals(firebaseUser.uid)) {
            followBtn.visibility = View.VISIBLE
        } else {
            checkFollow()
        }
        followBtn.setOnClickListener {
            val btn = followBtn.text.toString()
            if (btn.equals("follow")) {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(profileid!!).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileid!!)
                    .child("followers").child(firebaseUser.uid).setValue(true)
                addNotification()
            }
        }
        addPost.setOnClickListener {
            startActivity(Intent(context, writingPostActivity::class.java))
        }

        option.setOnClickListener {
            startActivity(Intent(context, OptionsActivity::class.java))
        }

        return view
    }

    private fun user() {
        val prefs: SharedPreferences = requireContext().getSharedPreferences("PREFS", MODE_PRIVATE)
        val profileid = prefs.getString("profileid", "none").toString()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Glide.with(context!!).load(user?.imageurl).into(main_img)
                username.text = user?.username
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid!!)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null) {
                    return
                }
                val user = snapshot.getValue(User::class.java)
                if (snapshot.exists()) {
                    Glide.with(context!!).load(user?.imageurl).into(main_img)
                    username.text = user?.username
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(profileid!!).exists()) {
                    followBtn.text = "following"
                } else {
                    followBtn.text = "follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addNotification() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Notifications").child(profileid!!)

        val hashMap:HashMap<String, Any> = HashMap()
        hashMap.put("userid",firebaseUser.uid)
        hashMap.put("text", "start follow")
        hashMap.put("postid", "")
        hashMap.put("ispost", false)

        reference.push().setValue(hashMap)
    }
}