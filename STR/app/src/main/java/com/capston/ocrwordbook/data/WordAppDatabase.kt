package com.capston.ocrwordbook.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(WordbookItem::class, Folder::class), version = 1)
abstract class WordAppDatabase : RoomDatabase() {
    abstract fun wordbookItemDao(): WordbookItemDao
    abstract fun folderDao(): FolderDao
}