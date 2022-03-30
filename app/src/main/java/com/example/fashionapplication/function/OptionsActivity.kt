package com.example.fashionapplication.function

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.fashionapplication.MainActivity
import com.example.fashionapplication.R
import com.example.fashionapplication.databinding.ActivityOptionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class OptionsActivity : AppCompatActivity() {

    private val PICK_IMAGE_FOR_ALBUM = 0
    private lateinit var storage : FirebaseStorage
    var uriPhoto : Uri? = null
    private val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityOptionsBinding

    var profileImg: CircleImageView = binding.optionProfileImg
    lateinit var profileid: String
    var username: TextView = binding.optionUsername
    var logout: TextView = binding.optionLogout
    var taltoe: TextView = binding.optionHoewontaltoe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs: SharedPreferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("profileid", "none").toString()
        userInfo()

        val changeProfile = binding.optionChangeProfile
        changeProfile.setOnClickListener {
            changeProfile()
        }

        logout.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            auth.signOut()  // 로그아웃 코드
        }

        // TODO :: firebase 회원 삭제하기
        taltoe.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
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
                Glide.with(this@OptionsActivity).load(user?.imageurl).into(binding.optionProfileImg)
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