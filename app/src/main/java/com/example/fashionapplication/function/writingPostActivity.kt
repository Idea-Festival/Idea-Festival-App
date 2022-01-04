package com.example.fashionapplication.function

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.R
import com.example.fashionapplication.data.PostDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.write_posting.*
import java.text.SimpleDateFormat
import java.util.*

class writingPostActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage:FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_posting)

        // 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("Posts")

        // image crop, open album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra("crop", true)
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // upload
        uploading_post.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_FROM_ALBUM -> {
                data?.data.let {
                    cropImage(it!!)     // 이미지를 선택했을 때 수행
                }
                photoUri = data?.data
                upload_img.setImageURI(photoUri)
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (requestCode == Activity.RESULT_OK) {
                    upload_img.setImageBitmap(result.bitmap)
                    upload_img.setImageURI(result.uri)
                    photoUri = result.uri
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "문제가 발생했습니다, 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> finish()
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this)
    }

    private fun contentUpload() {
        // file 형식 만들기
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_${timestamp}_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 업로드, 데이터베이스 추가
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var postDto = PostDto()

                // data 담기
                postDto.imageUrl = uri.toString()
                postDto.uid = auth?.currentUser?.uid
                val referenceRef: DatabaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users").child(auth!!.currentUser!!.uid).child("username")
                postDto.userId = referenceRef.toString()
                postDto.explain = comment_post.text.toString()
                postDto.timestamp = System.currentTimeMillis()
                reference?.child(postDto.userId!!)?.setValue(postDto)

                Toast.makeText(this, "업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
            }
        }
    }
}