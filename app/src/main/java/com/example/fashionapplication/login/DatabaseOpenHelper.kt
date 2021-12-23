package com.example.fashionapplication.login

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

lateinit var nameUser : String  // 유저 이름을 불러옴(다른곳에서 쓰게)
class DatabaseOpenHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        Log.i("tag", "db 생성_db가 없을때만 최초로 실행함")
        createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {}
    fun createTable(db: SQLiteDatabase) {
        val sql = "CREATE TABLE " + tableName + "(id text, pw text, name text, email text)"
        try {
            db.execSQL(sql)
        } catch (e: SQLException) {
        }
    }

    fun insertUser(db: SQLiteDatabase, id: String, pw: String, name: String, email: String) {
        Log.i("tag", "회원가입을 했을때 실행함")
        db.beginTransaction()
        try {
            val sql = "INSERT INTO " + tableName + "(id, pw, name, email)" +
                    "values('" + id + "', '" + pw + "', '" +name+ "', '" +email+ "')"
            Log.i("tag",sql)
            nameUser = name
            Log.i("tag", nameUser)
            db.execSQL(sql)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        const val tableName = "SNS_Users"
    }
}