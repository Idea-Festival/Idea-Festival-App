package com.example.fashionapplication.data

data class PostDto(
    var explain:String? = null,  // 컨텐츠의 설명을 관리
    var imageUrl: String? = null,    // 이미지를 관리
    var uid: String? = null,     // 어느 유저가 올렸는지 관리
    var userId:String? = null,   // 올린 유저의 이미지 관리
    var timestamp: String? = null,    // 몇 시에 올렸는지 관리
    var favoriteCount: Int = 0,  // 몇개의 좋아요를 받았는지 관리
    var tag1: String? = null,   // 해시태그들
    var tag2: String? = null,
    var tag3: String? = null,
    var bookmark: MutableMap<String, Boolean> = HashMap(),
    var favorite: MutableMap<String, Boolean> = HashMap()) {
    data class Comment(     // 댓글 관리 클래스
        var uri: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null
    )
}
