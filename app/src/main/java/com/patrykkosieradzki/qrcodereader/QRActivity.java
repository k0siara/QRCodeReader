package com.patrykkosieradzki.qrcodereader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

public class QRActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;

    private QRCodeReaderView mQRCodeReaderView;

    private ImageView flashlight;
    private ImageView rect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mQRCodeReaderView = findViewById(R.id.qr);

        rect = (ImageView) findViewById(R.id.qr_rect);

        if (DeviceUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            init();
        } else {
            DeviceUtils.requestPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA);
        }

        LinearLayout flashlightLayout = findViewById(R.id.layout_flashlight);
        flashlight = (ImageView) findViewById(R.id.flashlight);

        flashlightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashlight.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.flashlight_off).getConstantState()) {
                    flashlight.setImageResource(R.drawable.flashlight_on);
                    mQRCodeReaderView.setTorchEnabled(true);
                } else {
                    flashlight.setImageResource(R.drawable.flashlight_off);
                    mQRCodeReaderView.setTorchEnabled(false);
                }
            }
        });
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
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
        mQRCodeReaderView.setAutofocusInterval(10L);
        mQRCodeReaderView.setOnQRCodeReaderListener(new OnQRCodeReaderListener() {
            @Override
            public void onSuccess(Result result) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200L);

                Intent intent = new Intent();
                intent.putExtra("data", result.getText());
                setResult(MainActivity.QR_READ, intent);
                finish();
            }

            @Override
            public void onFailure() {

            }
        });
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
