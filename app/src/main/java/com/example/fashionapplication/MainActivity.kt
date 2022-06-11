package com.example.fashionapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.databinding.ActivityMainBinding
import com.example.fashionapplication.login.NewUserActivity
import com.example.fashionapplication.login.FindPasswordActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var databaseRef: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseFirestore.getInstance()

        binding.loginButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                val email:String = binding.email.text.toString()
                val pw:String = binding.password.text.toString()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pw)) {
                    Toast.makeText(this@MainActivity, "이메일과 비밀번호는 필수 입력사항 입니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this@MainActivity,
                    object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            Toast.makeText(this@MainActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                .putExtra("userId", auth.uid)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "이메일 또는 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })

        binding.newUserBtn.setOnClickListener {
            startActivity(Intent(this, NewUserActivity::class.java))
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out)
        }
        binding.findInformation.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out)
        }
    }
}