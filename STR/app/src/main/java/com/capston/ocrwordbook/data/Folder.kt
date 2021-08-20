package com.capston.ocrwordbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Folder(
    @PrimaryKey val folderId: String,
    @ColumnInfo(name = "folderName") val folderName: String,
    @ColumnInfo(name = "folderIds") var folderIds: String
)