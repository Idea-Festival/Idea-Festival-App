package com.example.fashionapplication.data

data class FollowDto(
    // 이 사람을 팔로잉하는 사람들
    var followerCount: Int = 0,
    var followers : MutableMap<String, Boolean> = HashMap(),

    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap()
)
