package com.example.fashionapplication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(id) || TextUtils.isEmpty(pw)) {
                        binding.warning.visibility = View.VISIBLE
                    }
                    if (pw.length < 6) {
                        Toast.makeText(this@NewUserActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                    }
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    val user:com.example.fashionapplication.data.UserData = com.example.fashionapplication.data.UserData()
                    user.uid = firebaseUser?.uid
                    user.email = firebaseUser?.email
                    user.pw = pw
                    database = FirebaseDatabase.getInstance()
                    reference = database.getReference("Users")

                    Toast.makeText(this@NewUserActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@NewUserActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
                } else {
                    Toast.makeText(this@NewUserActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}