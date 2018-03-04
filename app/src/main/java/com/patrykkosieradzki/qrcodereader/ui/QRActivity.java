package com.patrykkosieradzki.qrcodereader.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.patrykkosieradzki.qrcodereader.R;
import com.patrykkosieradzki.qrcodereader.utils.DeviceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 0;

    @BindView(R.id.scanner_view) protected ZXingScannerView mScannerView;
    @BindView(R.id.flashlight) protected ImageView flashlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ButterKnife.bind(this);

        checkCameraPermission();
        initScannerView();
    }

    private void checkCameraPermission() {
        if (!DeviceUtils.INSTANCE.hasPermission(this, Manifest.permission.CAMERA)) {
            DeviceUtils.INSTANCE.requestPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA);
        }
    }

    private void initScannerView() {
        mScannerView.setSquareViewFinder(true);
        mScannerView.setResultHandler(this);
        mScannerView.setLaserEnabled(false);
        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScannerView();
            } else {
                // TODO: Handle retarded user
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

        ParsedResult parsedResult = ResultParser.parseResult(result);
        switch (parsedResult.getType()) {
            case ADDRESSBOOK:
                break;

            case EMAIL_ADDRESS:
                break;

            case PRODUCT:
                break;

            case URI:
                break;

            case TEXT:
                break;

            case GEO:
                break;

            case TEL:
                break;

            case SMS:
                break;

            case CALENDAR:
                break;

            case WIFI:
                break;

            case ISBN:
                break;

            case VIN:
                break;

            default:
                break;
        }

        Intent intent = new Intent();
        intent.putExtra("text", result.getText());
        //intent.putExtra("type", );
        setResult(HomeActivity.QR_READ, intent);
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