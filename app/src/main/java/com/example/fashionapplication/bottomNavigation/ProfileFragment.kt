package com.example.fashionapplication.bottomNavigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fashionapplication.R
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private var mySaves: ArrayList<String> = arrayListOf()
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileid: String

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
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_profile, container, false)
        addPost = view.findViewById(R.id.add_post)
        option = view.findViewById(R.id.profile_setting)
        profileImg = view.findViewById(R.id.main_img)
        follower = view.findViewById(R.id.follower_count)
        followBtn = view.findViewById(R.id.profile_follow_btn)
        username = view.findViewById(R.id.user_name)

        addPost.setOnClickListener {
            startActivity(Intent(context, writingPostActivity::class.java))
        }

        return view
    }
}