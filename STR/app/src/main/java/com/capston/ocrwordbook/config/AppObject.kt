package com.capston.ocrwordbook.config

import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.capston.ocrwordbook.data.Folder
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.data.WordAppDatabase
import com.capston.ocrwordbook.data.WordbookItem

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

    val wordbookItemComparator = Comparator<WordbookItem> { o1, o2 ->
        if (o1.isWord != o2.isWord) {
            o1.isWord.compareTo(o2.isWord)
        } else if (o1.favorite != o2.favorite) {
            o1.favorite.compareTo(o2.favorite)
        } else {
            o1.word.compareTo(o2.word)
        }
    }


}