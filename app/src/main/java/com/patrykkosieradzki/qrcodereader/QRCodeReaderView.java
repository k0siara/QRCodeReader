package com.patrykkosieradzki.qrcodereader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

public class QRCodeReaderView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private QRCodeReader mQRCodeReader;
    private CameraManager mCameraManager;
    private OnQRCodeReaderListener mOnQRCodeReaderListener;

    public QRCodeReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (DeviceUtils.hasBackCamera(getContext())) {
            mCameraManager = new CameraManager(getContext());
            mCameraManager.setPreviewCallback(this);
            getHolder().addCallback(this);
            setBackCamera();
        }
    }

    public void setBackCamera() {
        mCameraManager.setPreviewCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public void setAutofocusInterval(long ms) {
        if (mCameraManager != null) {
            mCameraManager.setAutofocusInterval(ms);
        }
    }

    public void forceAutoFocus() {
        if (mCameraManager != null) {
            mCameraManager.forceAutoFocus();
        }
    }

    public void startCamera() {
        mCameraManager.startPreview();
    }

    public void stopCamera() {
        mCameraManager.stopPreview();
    }

    public void setmOnQRCodeReaderListener(OnQRCodeReaderListener onQRCodeReaderListener) {
        this.mOnQRCodeReaderListener = onQRCodeReaderListener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager.openDriver(surfaceHolder, getWidth(), getHeight());

            mQRCodeReader = new QRCodeReader();
            mCameraManager.startPreview();


        } catch (IOException e) {
            e.printStackTrace();

            mCameraManager.closeDriver();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }
}
