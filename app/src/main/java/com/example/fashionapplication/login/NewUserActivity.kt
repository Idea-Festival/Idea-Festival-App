package com.example.fashionapplication.login

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.fashionapplication.MainActivity
import com.example.fashionapplication.R
import com.example.fashionapplication.data.UserData
import com.example.fashionapplication.databinding.ActivityNewUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewUserActivity : AppCompatActivity() {

    private val version: Int = 1
    private lateinit var helper: DatabaseOpenHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var cursor: Cursor
    private lateinit var sql: String
    private lateinit var binding: ActivityNewUserBinding
//    private lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = DatabaseOpenHelper(this, DatabaseOpenHelper.tableName, null, version)
        database = helper.writableDatabase

        binding.createUserBtn.setOnClickListener(object : OnClickListener, View.OnClickListener {
            override fun onClick(v: View) {
                val id = binding.newId.text.toString()
                val pw = binding.newPw.text.toString()
                val email = binding.newEmail.text.toString()
                val name = binding.newName.text.toString()

                // 입력칸이 비었을때
                if (id.isEmpty() || email.isEmpty() || name.isEmpty() || pw.isEmpty()) {
                    val warn: TextView = findViewById(R.id.warning)
                    warn.visibility = View.VISIBLE
                    return
                }

                // 유저이름
                sql = "SELECT name FROM "+ DatabaseOpenHelper.tableName + " WHERE id = '" + id + "'"    // name 열에서 id 검색
                cursor = database.rawQuery(sql, null)
                if (cursor.count != 0) {
                    Toast.makeText(this@NewUserActivity, "존재하는 이름입니다.", Toast.LENGTH_SHORT).show()
                }

                // 이메일
                sql = "SELECT email FROM "+ DatabaseOpenHelper.tableName + " WHERE id = '" + id + "'"
                cursor = database.rawQuery(sql, null)
                if (cursor.count != 0) {
                    Toast.makeText(this@NewUserActivity, "존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
                }

                // 아이디
                sql = "SELECT id FROM "+ DatabaseOpenHelper.tableName + " WHERE id = '" + id + "'"
                cursor = database.rawQuery(sql, null)
                // 아이디가 존재할때
                if (cursor.count != 0) {
                    Toast.makeText(this@NewUserActivity, "존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                }else {
//                    firebaseDatabase = FirebaseDatabase.getInstance().getReference("user")
//                    val user = UserData(id,pw,name,email)
//                    firebaseDatabase.child(name).push().setValue(user)

                    helper.insertUser(database, id, pw, name, email)
                    val intent = Intent(this@NewUserActivity, MainActivity::class.java)
                    Toast.makeText(this@NewUserActivity, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
                }
            }
        })
        binding.addCancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
        }
    }
}