package com.patrykkosieradzki.qrcodereader.extensions

import android.util.SparseBooleanArray

fun SparseBooleanArray.contains(position: Int, valueIfKeyNotFound: Boolean = false): Boolean {
    return this.get(position, valueIfKeyNotFound)
}