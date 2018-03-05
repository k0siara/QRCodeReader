package com.patrykkosieradzki.qrcodereader.application

import android.app.Application

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.squareup.leakcanary.LeakCanary

class QRCodeReaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        setupLeakCanary()
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        LeakCanary.install(this)
    }
}
