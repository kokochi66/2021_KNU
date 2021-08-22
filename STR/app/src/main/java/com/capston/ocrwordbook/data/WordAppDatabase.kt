package com.capston.ocrwordbook.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Word::class, Folder::class), version = 1)
abstract class WordAppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun folderDao(): FolderDao
}