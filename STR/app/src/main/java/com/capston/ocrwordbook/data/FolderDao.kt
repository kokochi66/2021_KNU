package com.capston.ocrwordbook.data

import androidx.room.*

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folder WHERE folderId = :folderId")
    suspend fun getFolder(folderId: String): Folder

    @Query("SELECT * FROM folder")
    suspend fun getAllFolder(): List<Folder>

}