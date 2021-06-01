package com.capston.ocrwordbook.ui.result.recycler

import android.graphics.Bitmap

data class ResultRecyclerItem(
    val img : Bitmap,
    val recognizedText : String,
    val meaningText : String
)


