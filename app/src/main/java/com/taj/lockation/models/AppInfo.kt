package com.taj.lockation.models

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(val name:String, val packageName:String,val isSystem:Boolean, val icon:ImageBitmap)
