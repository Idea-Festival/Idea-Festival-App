package com.example.fashionapplication.function

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.MainPageActivity
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.databinding.ActivityWritingPostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class writingPostActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage:FirebaseStorage? = null
    var photoUri: Uri? = null
    lateinit var fireStore: FirebaseFirestore
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null
    private lateinit var binding: ActivityWritingPostingBinding
    private val postDto = PostDto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWritingPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Posts")

        fireStore = FirebaseFirestore.getInstance()

        // image crop, open album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // upload
        binding.uploadingPost.setOnClickListener {
            contentUpload()
        }

        binding.backImg.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            photoUri = data?.data
            Log.d("SUCCESS", "onActivityResult photo uri: $photoUri")
            binding.uploadImg.setImageURI(photoUri)
        } else {
            Log.d("FAIL", "onActivityResult: 문제가 발생함")
            finish()
        }
    }

    private fun contentUpload() {
        // file 형식 만들기
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_${timestamp}_.png"
        val storageRef = storage?.reference?.child("images")?.child(auth?.uid!!)?.child(imageFileName)

        // 업로드, 데이터베이스 추가
        storage?.reference?.child("profileImage")?.child(auth?.uid!! + ".png")?.downloadUrl
            ?.addOnSuccessListener { imageUri ->
                postDto.profileImg = imageUri.toString()
            }

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                postDto.imageUrl = uri.toString()
                getData()
            }
        }
    }

    private fun getData() {
        // data 담기
        postDto.explain = binding.commentPost.text.toString()
        postDto.uid = auth?.currentUser?.uid
        postDto.userId = auth?.currentUser?.email
        postDto.tag1 = binding.hashiText1.text.toString()
        postDto.tag2 = binding.hashiText2.text.toString()
        postDto.tag3 = binding.hashiText3.text.toString()

        val time = System.currentTimeMillis()
        val date = Date(time)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:MM")
        postDto.timestamp = dateFormat.format(date)

        fireStore.collection("posts").document().set(postDto).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this, MainPageActivity::class.java))
            }
        }
    }

}