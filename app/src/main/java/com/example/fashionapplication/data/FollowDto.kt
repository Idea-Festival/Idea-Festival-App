package com.example.fashionapplication.data

data class FollowDto(
    // 이 사람을 팔로잉하는 사람들
    var followers : MutableMap<String, Boolean> = HashMap()
)
