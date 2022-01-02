package com.example.fashionapplication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.fashionapplication.MainActivity
import com.example.fashionapplication.R
import com.example.fashionapplication.databinding.ActivityNewUserBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.example.fashionapplication.MainPageActivity
import java.util.*
import kotlin.collections.HashMap

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseFirestore.getInstance()

        binding.createUserBtn.setOnClickListener {
            val name = binding.newName.text.toString()
            val email = binding.newEmail.text.toString()
            val id = binding.newId.text.toString()
            val pw = binding.newPw.text.toString()

            createUser(name,email,id,pw)
        }

        binding.addCancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
        }
    }

    private fun createUser(name:String, email:String, id:String, pw:String) {
        auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(this, object: OnCompleteListener<AuthResult>{
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    val userid = firebaseUser?.uid
                    val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userid!!)
                    val hashMap: HashMap<String, Any> = HashMap()

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(id) || TextUtils.isEmpty(pw)) {
                        binding.warning.visibility = View.VISIBLE
                    }
                    if (pw.length < 6) {
                        Toast.makeText(this@NewUserActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                    }

                    hashMap.put("id",userid)
                    hashMap.put("username", name.lowercase(Locale.getDefault()))
                    hashMap.put("email",email)
                    hashMap.put("pw",pw)
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/pestival-d14d7.appspot.com/o/profile_img.png?alt=media&token=327b430d-9cb1-4151-9eee-2d4300cd9cf9")

                    reference.setValue(hashMap).addOnCompleteListener(object: OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful) {
                                Toast.makeText(this@NewUserActivity, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@NewUserActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    })
                } else {
                    try {
                        p0.result
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@NewUserActivity, "이미 있는 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}