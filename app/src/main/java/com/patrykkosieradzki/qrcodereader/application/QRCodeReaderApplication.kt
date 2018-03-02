package com.patrykkosieradzki.qrcodereader.application

import android.app.Application

import com.google.firebase.FirebaseApp
import com.squareup.leakcanary.LeakCanary

class QRCodeReaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        setupLeakCanary()
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        LeakCanary.install(this)
    }
}
