package com.example.fashionapplication.function

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.write_posting.*
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class writingPostActivity: AppCompatActivity() {

    private var viewProfile: View? = null
    private lateinit var uploadImg: ImageView
    private lateinit var back:ImageView
    private lateinit var upload: ImageView
    private lateinit var add: TextView
    var pickImageFromAlbum = 0
    private lateinit var storage : FirebaseStorage
    var uriPhoto : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_posting)

        viewProfile = layoutInflater.inflate(R.layout.write_posting, constraintLayout, false)
        uploadImg = findViewById(R.id.upload_img)
        back = findViewById(R.id.back_img)
        storage = FirebaseStorage.getInstance()
        upload = findViewById(R.id.upload)
        add = findViewById(R.id.add)
        storage = FirebaseStorage.getInstance()
        uploadImg.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }

        back.setOnClickListener {
            finish()        // 액티비티 종료
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imgFileName = "IMAGE_" + timeStamp + "_.png"

        upload.visibility = View.INVISIBLE
        add.visibility = View.INVISIBLE
        if (requestCode == pickImageFromAlbum) {
            val file: Uri? = data?.data
            val storageRef:StorageReference = storage.reference                 // 저장소 변수
            val riversRef: StorageReference = storageRef.child(imgFileName)     // 저장할 사진
            val uploadTask: UploadTask? = file?.let { riversRef.putFile(it) }   // 파일을 넣을 변수

            val inputStream: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
            val img: Bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            uploadImg.setImageBitmap(img)

            uploading_post.setOnClickListener {
                uploading_post.setOnClickListener {
                    uploadTask?.addOnFailureListener {
                        Toast.makeText(view.context, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }?.addOnSuccessListener {
                        Toast.makeText(view.context, "업로드 완료", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        }
    }
}
