package com.capston.ocrwordbook.data

import androidx.room.*

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folder WHERE parentFolderId = :parentFolderId")
    suspend fun getChildFolders(parentFolderId: Long): List<Folder>?

    @Query("SELECT * FROM folder")
    suspend fun getAllFolder(): List<Folder>

    @Query("SELECT * FROM folder WHERE uid=:uid")
    suspend fun getFolderByUid(uid: Long): Folder

    @Query("DELETE FROM folder WHERE uid=:uid")
    suspend fun deleteFolderByUid(uid: Long)

    @Query("DELETE FROM folder WHERE parentFolderId=:uid")
    suspend fun deleteChildFoldersByUid(uid: Long)

}