package com.capston.ocrwordbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Folder(
    @ColumnInfo(name = "parentFolderId") var parentFolderId: Long,
    @ColumnInfo(name = "folderName") val folderName: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
}

// parent folder id
// 폴더 안의 폴더는 현재 폴더 id 를 parent folder id
//
// https://developer.android.com/training/data-storage/room/defining-data
// id1,id2,id3, ... <-> [id1, id2, id3, ]
//
