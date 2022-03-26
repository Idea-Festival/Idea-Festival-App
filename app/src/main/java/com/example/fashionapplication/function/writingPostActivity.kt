package com.example.fashionapplication.function

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.databinding.WritePostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import java.text.SimpleDateFormat
import java.util.*

class writingPostActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage:FirebaseStorage? = null
    var photoUri: Uri? = null
    lateinit var fireStore: FirebaseFirestore
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null
    private lateinit var binding: WritePostingBinding
    private val postDto = PostDto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Posts")

        fireStore = FirebaseFirestore.getInstance()

        // image crop, open album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra("crop", true)
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
        when (requestCode) {
            PICK_IMAGE_FROM_ALBUM -> {
                photoUri = data?.data
                binding.uploadImg.setImageURI(photoUri)

            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (requestCode == Activity.RESULT_OK) {
                    binding.uploadImg.setImageBitmap(result.bitmap)
                    binding.uploadImg.setImageURI(result.uri)
                    photoUri = result.uri
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "문제가 발생했습니다, 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> finish()
        }
    }

    private fun contentUpload() {
        // file 형식 만들기
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_${timestamp}_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 업로드, 데이터베이스 추가
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
            }
        }
    }

}