package com.patrykkosieradzki.qrcodereader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.patrykkosieradzki.qrcodereader.R;

public class MainActivity extends AppCompatActivity {

    public static final int QR_READ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, QRActivity.class), QR_READ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == QR_READ) {
            String text = data != null ? data.getExtras().get("data").toString() : "No QR Code Found.";
            Snackbar.make(findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}
