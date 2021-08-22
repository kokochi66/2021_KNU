package com.capston.ocrwordbook.data

// RecyclerView 에 사용 되는 데이터 클래스
data class WordbookItem (
    val uid: Long,
    var parentFolderId: Long,
    val word: String,
    val meaning: String = "",
    var favorite: Boolean = false,
    val isWord: Boolean = true // false 면 폴더이다.
)
