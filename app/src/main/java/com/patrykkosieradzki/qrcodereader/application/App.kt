package com.patrykkosieradzki.qrcodereader.application

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.patrykkosieradzki.qrcodereader.R
import com.squareup.leakcanary.LeakCanary

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    // fake DI
    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var mDatabase: DatabaseReference

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        initFakeDI()
        setupLeakCanary()
    }

    private fun initFakeDI() {
        instance = this

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mDatabase = FirebaseDatabase.getInstance().reference
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        LeakCanary.install(this)
    }
}
