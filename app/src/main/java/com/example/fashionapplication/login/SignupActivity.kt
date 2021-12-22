package com.example.fashionapplication.login

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapplication.MainActivity
import com.example.fashionapplication.R

class SignupActivity : AppCompatActivity() {
    private val version:Int = 1
    private lateinit var helper: DatabaseOpenHelper
    private lateinit var database: SQLiteDatabase
    lateinit var sql: String
    lateinit var cursor: Cursor
    private lateinit var check: Any

    private lateinit var cancel_btn: View
    private lateinit var Find_btn: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigup)

        helper = DatabaseOpenHelper(this, DatabaseOpenHelper.tableName, null, version)
        database = helper.writableDatabase
        Find_btn = findViewById(R.id.find_info_btn)
        Find_btn.setOnClickListener(object : OnClickListener, View.OnClickListener {
            override fun onClick(v: View) {
                val dataEmail: EditText = findViewById(R.id.new_email)
                val dataName: EditText = findViewById(R.id.new_name)
                val email: String = dataEmail.text.toString()
                val name: String = dataName.text.toString()

                // 입력칸이 비었을때
                if (email.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this@SignupActivity, "이메일과 이름은 필수 입력사항 입니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                val showId: View = findViewById(R.id.show_information)
                val showIdText: TextView = findViewById(R.id.information_id)
                val showPass: View = findViewById(R.id.show_information)
                val showPassText: TextView = findViewById(R.id.information_password)
                val okBtn: View = findViewById(R.id.ok_btn)
                val okText: TextView = findViewById(R.id.ok_text)
                // 아이디 찾기
                sql = "SELECT id FROM "+ DatabaseOpenHelper.tableName + " WHERE email = '" + email + "' AND name = '"+name+"'"
                cursor = database.rawQuery(sql, null)
                check = cursor.count
                Log.i("tag", cursor.toString())
                if (check == 0) {
                    Toast.makeText(this@SignupActivity,"정보가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                } else {
                    cursor.moveToFirst()
                    showId.visibility = View.VISIBLE
                    showIdText.visibility = View.VISIBLE
                    showIdText.text = "아이디는 "+cursor.getString(0)+" 입니다."
                    okBtn.visibility = View.VISIBLE
                    okText.visibility = View.VISIBLE
                }

                // 비번
                sql = "SELECT pw FROM "+ DatabaseOpenHelper.tableName + " WHERE email = '" + email + "' AND name = '"+name+"'"
                cursor = database.rawQuery(sql, null)
                check = cursor.count
                Log.i("tag", cursor.toString())
                if (check != 0) {
                    cursor.moveToFirst()
                    showPass.visibility = View.VISIBLE
                    showPassText.visibility = View.VISIBLE
                    showPassText.text = "비밀번호는 "+cursor.getString(0)+" 입니다."
                    okBtn.visibility = View.VISIBLE
                    okText.visibility = View.VISIBLE
                }
                okBtn.setOnClickListener {
                    val intent = Intent(this@SignupActivity,MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
                }
            }
        })

        cancel_btn = findViewById(R.id.cancel_btn)
        cancel_btn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down, R.anim.fade_out)
        }
    }
}