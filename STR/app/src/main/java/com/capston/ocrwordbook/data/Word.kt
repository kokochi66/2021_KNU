package com.capston.ocrwordbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "meaning") val meaning: String,
    @ColumnInfo(name = "favorite") var favorite: Boolean,
    @ColumnInfo(name = "isWord") val isWord: Boolean = true, // false 이면 폴더이다.
    @ColumnInfo(name = "folderName") val folderName: String = "Main"
)
