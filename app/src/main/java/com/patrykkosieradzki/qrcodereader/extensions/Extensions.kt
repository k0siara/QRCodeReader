package com.patrykkosieradzki.qrcodereader.extensions

fun<T: Any> T.getClassName(): String {
    return javaClass.kotlin.simpleName!!
}