package com.example.fashionapplication.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

@IgnoreExtraProperties
class UserData {
    var id: String? = null
    var pw: String? = null
    var name: String? = null
    var email: String? = null

    constructor(id: String, pw: String, name: String, email: String) {
        this.id = id
        this.pw = pw
        this.name = name
        this.email = email
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["id"] = id
        result["pw"] = pw
        result["name"] = name
        result["email"] = email
        return result
    }
}