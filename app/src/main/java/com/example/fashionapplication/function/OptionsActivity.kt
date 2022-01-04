package com.example.fashionapplication.function

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.fashionapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_options.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.write_posting.*
import java.text.SimpleDateFormat
import java.util.*

class OptionsActivity : AppCompatActivity() {

    private val PICK_IMAGE_FOR_ALBUM = 0
    var firebaseuser: FirebaseUser? = null
    private lateinit var storage : FirebaseStorage
    var uriPhoto : Uri? = null
    private val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)

    lateinit var profileImg: CircleImageView
    lateinit var profileid: String
    lateinit var username: TextView
    lateinit var logout: TextView
    lateinit var taltoe: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        profileImg = findViewById(R.id.option_profile_img)
        username = findViewById(R.id.option_username)
        storage = FirebaseStorage.getInstance()
        firebaseuser = FirebaseAuth.getInstance().currentUser
        val prefs: SharedPreferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("profileid", "none").toString()
        userInfo()

        taltoe = findViewById(R.id.option_hoewontaltoe)
        logout = findViewById(R.id.option_logout)

        val changeProfile = findViewById<LinearLayout>(R.id.option_change_profile)
        changeProfile.setOnClickListener {
            changeProfile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FOR_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // 이미지 경로를 불러옴
                uriPhoto = data?.data
                profileImg.setImageURI(uriPhoto)

                val save: ConstraintLayout = findViewById(R.id.option_save)
                save.visibility = View.VISIBLE
                save.setOnClickListener {
                    saveProfile()
                }
            } else {
                // 취소
                finish()
            }
        }
    }

    private fun userInfo() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(com.example.fashionapplication.data.User::class.java)
                Glide.with(this@OptionsActivity).load(user?.imageurl).into(option_profile_img)
                username.text = user?.username
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun changeProfile() {
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photoIntent = Intent(Intent.ACTION_PICK)
                photoIntent.type = "image/*"
                startActivityForResult(photoIntent, PICK_IMAGE_FOR_ALBUM)
                contentUpload()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun contentUpload() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHss").format(Date())
        val imageFileName = "IMAGE_$timestamp.png"
        val storageRef = storage.reference.child("images").child(imageFileName)

        storageRef.child("imageurl").putFile(uriPhoto!!)
    }

    private fun saveProfile() {

    }
}