package com.example.fashionapplication.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.databinding.ActivitySigupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseFirestore.getInstance()

        binding.findInfoBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val email = binding.findEmail.text.toString()

                if (email.isEmpty()) {
                    Toast.makeText(this@FindPasswordActivity, "이메일은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@FindPasswordActivity, "비밀번호 재설정 메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.w("tag", task.exception)
                        }
                    }
                }
            }
        })
    }
}