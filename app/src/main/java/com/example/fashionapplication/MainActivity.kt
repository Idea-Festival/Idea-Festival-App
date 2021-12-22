package com.example.fashionapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.fashionapplication.login.DatabaseOpenHelper
import com.example.fashionapplication.login.NewUserActivity
import com.example.fashionapplication.login.OnClickListener
import com.example.fashionapplication.login.SignupActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.new_email

class MainActivity : AppCompatActivity() {
    private lateinit var find_btn: TextView
    private lateinit var new_user_btn: TextView

    val version:Int = 1
    private lateinit var helper: DatabaseOpenHelper
    private lateinit var database: SQLiteDatabase
    lateinit var sql: String
    lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helper = DatabaseOpenHelper(this, DatabaseOpenHelper.tableName, null, version)
        database = helper.writableDatabase

        // intent
        find_btn = findViewById(R.id.find_information)
        new_user_btn = findViewById(R.id.new_user)
        find_btn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out)      // 액티비티 변환 애니메이션
        }
        new_user_btn.setOnClickListener {
            startActivity(Intent(this,NewUserActivity::class.java))
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out)
        }

        // 로그인 이벤트
        login_button.setOnClickListener(object : OnClickListener, View.OnClickListener {
            override fun onClick(v: View) {
                val id: String = new_email.text.toString()
                val pw: String = password.text.toString()

                // 입력칸이 비었을때
                if (id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(this@MainActivity, "아이디와 비밀번호는 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                sql = "SELECT id FROM "+ DatabaseOpenHelper.tableName + " WHERE id = '" + id + "'"
                Log.i("tag", sql)
                cursor = database.rawQuery(sql, null)

                // 아이디가 틀릴때
                if (cursor.count != 1) {
                    Toast.makeText(this@MainActivity, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                sql = "SELECT pw FROM "+ DatabaseOpenHelper.tableName + " WHERE id = '" + id + "'"
                cursor = database.rawQuery(sql, null)

                cursor.moveToNext()
                // 비밀번호가 다를 때
                if (!pw.equals(cursor.getString(0))) {
                    Toast.makeText(this@MainActivity, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                    return
                }else {
                    Toast.makeText(this@MainActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, MainPageActivity::class.java))
                    finish()
                }
                cursor.close()
            }
        })
    }
}