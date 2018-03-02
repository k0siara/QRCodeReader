package com.patrykkosieradzki.qrcodereader.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.squareup.leakcanary.LeakCanary;

public class QRCodeReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        setupLeakCanary();
    }

    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
    }
}
