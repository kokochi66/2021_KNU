package com.capston.ocrwordbook.data

import androidx.room.*

@Dao
interface WordbookItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordbookItem(wordbookItem: WordbookItem)

    @Query("SELECT * FROM wordbookitem WHERE belongedFolderId=:belongedFolderId")
    suspend fun getAllBelongedWordbookItem(belongedFolderId: String): List<WordbookItem>

    @Delete
    suspend fun deleteWordbookItem(wordbookItem: WordbookItem)

    @Query("DELETE FROM wordbookitem WHERE belongedFolderId=:belongedFolderId")
    suspend fun deleteAllBelongedWordbookItem(belongedFolderId: String)

    @Query("SELECT * FROM wordbookitem WHERE word LIKE :keyword")
    suspend fun searchWord(keyword: String): List<WordbookItem>

    @Query("UPDATE wordbookitem SET belongedFolderId=:nextFolderId WHERE uid=:uid")
    suspend fun updateWordbookItemPosition(uid: String, nextFolderId: String)


    @Query("SELECT * FROM wordbookitem")
    suspend fun getAllWordbookItem(): List<WordbookItem>

}