package com.capston.ocrwordbook.config

import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.capston.ocrwordbook.data.WordAppDatabase

object AppObject {

    fun getWordAppDatabase(context: Context): WordAppDatabase {
        return Room.databaseBuilder(
            context,
            WordAppDatabase::class.java,
            "wordDatabase"
        ).build()
    }

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}