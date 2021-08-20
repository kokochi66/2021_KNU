package com.capston.ocrwordbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordbookItem (
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "belongedFolderId") val belongedFolderId: String = "",
    @ColumnInfo(name = "word")val word: String,
    @ColumnInfo(name = "meaning") val meaning: String = "",
    @ColumnInfo(name = "favorite") var favorite: Boolean = false,
    @ColumnInfo(name = "isWord") val isWord: Boolean = true, // false 면 폴더이다.
    @ColumnInfo(name = "nextFolderId") val nextFolderId: String = "" // 이동할 폴더아이디다.
)
