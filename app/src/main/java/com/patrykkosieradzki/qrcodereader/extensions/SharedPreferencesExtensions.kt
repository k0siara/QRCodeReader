package com.patrykkosieradzki.qrcodereader.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

inline fun Activity.getPreferences(mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getPreferences(mode)
}

inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.func()
    editor.apply()
}

