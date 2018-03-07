package com.patrykkosieradzki.qrcodereader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.patrykkosieradzki.qrcodereader.R;
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Class<?> cls;
        if (FirebaseAuth.getInstance().getCurrentUser() == null && !isLoggedIn()) {
            cls = LoginActivity.class;
        } else {
            cls = HomeActivity.class;
        }

        startActivity(new Intent(MainActivity.this, cls));
        finish();
    }

    private boolean isLoggedIn() {
        return getPreferences(Context.MODE_PRIVATE).getInt("logged_in", 0) == 1;
    }
}
