package xyz.srcbox.lin.data

import android.graphics.Bitmap
import xyz.srcbox.lin.view.StokeRect

data class ShowIconData(
    val bitmap: Bitmap,
    val stokeRect: StokeRect,
    val resourceIdName: String
)