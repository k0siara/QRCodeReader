package com.patrykkosieradzki.qrcodereader.repository


interface Repository<in T> {

    fun add(item: T, listener: OnCompleteListener? = null)
    fun remove(item: T, listener: OnCompleteListener? = null)
}