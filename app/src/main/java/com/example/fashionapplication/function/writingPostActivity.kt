package com.example.fashionapplication.function

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.databinding.WritePostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import java.text.SimpleDateFormat
import java.util.*

class writingPostActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage:FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null
    private lateinit var binding: WritePostingBinding
    private val hashMap: HashMap<String, Any> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Posts")

        Log.d("TAG", "intent: ${intent.getStringExtra("username")}")
        Log.d("TAG", "intent: ${intent.getSerializableExtra("profileImage")}")

        // image crop, open album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
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
            storageRef.downloadUrl.addOnSuccessListener {
                getData()
            }
        }
    }

    private fun getData() {
        // data 담기
//        val referenceRef: DatabaseReference = FirebaseDatabase.getInstance().reference
//            .child("Users").child(auth?.uid.toString()).child("username")
//        referenceRef.addListenerForSingleValueEvent(object :ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val value = snapshot.getValue(String::class.java).toString()
//                hashMap.put("profileImage", value)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "onCancelled: error!!")
//            }
//        })
//
//        val imageRef = FirebaseDatabase.getInstance().reference
//            .child("Users").child(auth?.uid.toString()).child("imageurl")
//        imageRef.addListenerForSingleValueEvent(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val value = snapshot.getValue(String::class.java).toString()
//                hashMap.put("profileImage", value)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "onCancelled: error!!")
//            }
//        })

        hashMap.put("explain", binding.commentPost.text.toString())
        hashMap.put("tag1", binding.hashiText1.text.toString())
        hashMap.put("tag2", binding.hashiText2.text.toString())
        hashMap.put("tag3", binding.hashiText3.text.toString())
        hashMap.put("like", "0")

        val time = System.currentTimeMillis()
        val date = Date(time)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:MM")
        hashMap.put("time", dateFormat.format(date))

        reference?.child(auth?.uid!!)?.setValue(hashMap)?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("TAG", "hashMap: $hashMap")
        hashMap.clear()
        finish()
    }
}