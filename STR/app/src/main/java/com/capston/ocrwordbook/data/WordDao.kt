package com.capston.ocrwordbook.data

import androidx.room.*

@Dao
interface WordDao {
    @Query("SELECT * FROM word WHERE folderName == :folderName")
    fun getAllWords(folderName: String = "Main"): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word)

    @Delete
    fun deleteWord(word: Word)

}