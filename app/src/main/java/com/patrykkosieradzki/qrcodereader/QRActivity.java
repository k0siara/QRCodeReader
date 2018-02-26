package com.patrykkosieradzki.qrcodereader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 0;

    @BindView(R.id.scanner_view) ZXingScannerView mScannerView;
    @BindView(R.id.flashlight) ImageView flashlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ButterKnife.bind(this);

        checkCameraPermission();
        initScannerView();
    }

    private void checkCameraPermission() {
        if (!DeviceUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            DeviceUtils.requestPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA);
        }
    }

    private void initScannerView() {
        mScannerView.setLaserEnabled(false);
        mScannerView.setSquareViewFinder(true);
        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initScannerView();

                } else {

                }
            }
        }
    }

    @OnClick(R.id.layout_flashlight)
    public void onFlashlightLayoutClick() {
        if (flashlight.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.flashlight_off).getConstantState()) {
            flashlight.setImageResource(R.drawable.flashlight_on);
            mScannerView.setFlash(true);
        } else {
            flashlight.setImageResource(R.drawable.flashlight_off);
            mScannerView.setFlash(false);
        }
    }

    @Override
    public void handleResult(Result result) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);

        Intent intent = new Intent();
        intent.putExtra("data", result.getText());
        setResult(MainActivity.QR_READ, intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

}