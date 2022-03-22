package com.example.fashionapplication.data

import com.google.firebase.database.DataSnapshot

interface OnGetDataListener {
    fun onSuccess(dataSnapShot: DataSnapshot)
    fun onStart()
    fun onFailure()
}