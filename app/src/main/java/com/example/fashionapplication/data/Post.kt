package com.example.fashionapplication.data

data class Post (
    var postid:String,
    var publisher:String,
    var postimage:String,
    var description:String,
    var tag1: String? = null,
    var tag2: String? = null,
    var tag3: String? = null
)