package com.patrykkosieradzki.qrcodereader


interface Repository<T> {
    fun add(item: T)
    fun remove(item: T)
}