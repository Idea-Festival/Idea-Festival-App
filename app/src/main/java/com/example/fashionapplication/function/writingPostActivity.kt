package com.example.fashionapplication.function

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.R
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.data.PostDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.write_posting.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class writingPostActivity: AppCompatActivity() {

    val PICK_IMAGE_FOR_ALBUM = 0
    private lateinit var storage : FirebaseStorage
    var uriPhoto : Uri? = null
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_posting)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val back = findViewById<ImageView>(R.id.back_img)
        back.setOnClickListener {
            finish()
        }

        // album 열기
        upload_img.setOnClickListener {
            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, PICK_IMAGE_FOR_ALBUM)
        }

        // 업로드
        uploading_post.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FOR_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // 이미지 경로를 불러옴
                uriPhoto = data?.data
                upload_img.setImageURI(uriPhoto)
                upload.visibility = View.INVISIBLE
                add.visibility = View.INVISIBLE
            } else {
                // 취소
                finish()
            }
        }
    }

    private fun contentUpload() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHss").format(Date())
        val imageFileName = "IMAGE_$timestamp.png"
        val storageRef = storage.reference.child("images").child(imageFileName)

        // upload
        storageRef.putFile(uriPhoto!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var postDto = PostDto()
                val a = intent.getStringArrayExtra("userName")
                Log.d("tag", a.toString())

                postDto.imageUrl = uri.toString()
                postDto.uid = auth.currentUser?.uid
                postDto.userId = auth.currentUser?.email
                postDto.explain = comment_post.text.toString()
                postDto.tag1 = hashi_text1.text.toString()
                postDto.tag2 = hashi_text2.text.toString()
                postDto.tag3 = hashi_text3.text.toString()
                postDto.timestamp = System.currentTimeMillis()
                firestore.collection("images").document().set(postDto)
                setResult(Activity.RESULT_OK)
                Toast.makeText(this,"업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
