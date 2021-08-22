package com.capston.ocrwordbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {


    @Query("SELECT * FROM word WHERE parentFolderId=:parentFolderId")
    suspend fun getChildWords(parentFolderId: Long): List<Word>?

    @Query("DELETE FROM word WHERE uid=:uid")
    suspend fun deleteWordByUid(uid: Long)

    @Query("DELETE FROM word WHERE parentFolderId=:uid")
    suspend fun deleteChildWordsByUid(uid: Long)

    @Query("SELECT * FROM word WHERE uid=:uid")
    suspend fun getWordByUid(uid: Long): Word

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    //@Query("SELECT * FROM word WHERE word LIKE :keyword")
    //suspend fun searchWords(keyword: String): List<Word>?

}