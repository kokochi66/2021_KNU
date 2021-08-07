package com.capston.ocrwordbook.data

data class Word(
    val id: String,
    val word: String,
    val meaning: String,
    var favorite: Boolean
)
