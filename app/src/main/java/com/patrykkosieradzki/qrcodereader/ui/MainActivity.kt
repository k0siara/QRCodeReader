package com.patrykkosieradzki.qrcodereader.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.google.firebase.auth.FirebaseAuth
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.extensions.getPreferences
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity
import org.jetbrains.anko.startActivity

class MainActivity : Activity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser == null && !isLoggedIn()) {
            startActivity<LoginActivity>()
        } else {
            startActivity<HomeActivity>()
        }

        finish()
    }

    private fun isLoggedIn(): Boolean {
        return getPreferences().getInt("logged_in", 0) == 1
    }


}
