package com.capston.ocrwordbook.ui.word.recylcer

data class WordRecyclerItem(
        val id: String,
        val word: String,
        val meaning : String,
        var favorite: Boolean
)