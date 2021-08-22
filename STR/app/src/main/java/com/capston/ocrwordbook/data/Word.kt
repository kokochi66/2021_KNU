package com.capston.ocrwordbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class Word (
    @ColumnInfo(name = "parentFolderId") var parentFolderId: Long,
    @ColumnInfo(name = "word")val word: String,
    @ColumnInfo(name = "meaning") val meaning: String = "",
    @ColumnInfo(name = "favorite") var favorite: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
}