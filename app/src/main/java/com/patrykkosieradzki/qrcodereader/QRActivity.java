package com.patrykkosieradzki.qrcodereader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QRActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;

    private QRCodeReaderView mQRCodeReaderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mQRCodeReaderView = findViewById(R.id.qr);

        if (DeviceUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            init();
        } else {
            DeviceUtils.requestPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    init();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void init() {
        mQRCodeReaderView.setAutofocusInterval(1000L);
        //mQRCodeReaderView.setOnQRCodeReadListener(this);
        mQRCodeReaderView.startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mQRCodeReaderView != null) {
            mQRCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mQRCodeReaderView != null) {
            mQRCodeReaderView.stopCamera();
        }
    }
}
