package com.example.fashionapplication.function

import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fashionapplication.R
import com.google.firebase.database.core.view.View
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.write_posting.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class writingPostActivity: AppCompatActivity() {

    private lateinit var uploadImg: ImageView
    private lateinit var back:ImageView
    var pickImageFromAlbum = 0
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_posting)

        uploadImg = findViewById(R.id.upload_img)
        back = findViewById(R.id.back_img)
        fbStorage = FirebaseStorage.getInstance()

        uploadImg.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }

        back.setOnClickListener {
            finish()        // 액티비티 종료
        }
    }
    private fun funImageUpload(view: ImageView) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imgFileName = "IMAGE_" + timeStamp + "_.png"
        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(view.context, "업로드 완료", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageFromAlbum) {
            if (resultCode == RESULT_OK) {
                uriPhoto = data?.data
                uploadImg.setImageURI(uriPhoto)

                if (checkSelfPermission(uploadImg.toString())
                    == PackageManager.PERMISSION_GRANTED){
                    funImageUpload(uploadImg)
                }
            }
        }
    }
}