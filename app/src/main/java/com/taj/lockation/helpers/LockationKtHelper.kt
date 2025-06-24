package com.taj.lockation.helpers

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

class LockationKtHelper
{
}

fun DrawableToImgBitmap(d: Drawable) : ImageBitmap
{
	return d.toBitmap().asImageBitmap();
}

// SharedPreferences extension functions

fun SharedPreferences.set(key:String, value:Boolean)
{
	this.edit().putBoolean(key,value).apply();
}

fun SharedPreferences.set(key:String, value:String)
{
	this.edit().putString(key,value).apply();
}
fun SharedPreferences.set(key:String, value:Long)
{
	this.edit().putLong(key,value).apply();
}

fun SharedPreferences.set(key:String, value:Int)
{
	this.edit().putInt(key,value).apply();
}