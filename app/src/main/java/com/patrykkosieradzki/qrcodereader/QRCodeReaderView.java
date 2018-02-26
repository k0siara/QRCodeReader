package com.patrykkosieradzki.qrcodereader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

public class QRCodeReaderView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private QRCodeReader mQRCodeReader;
    private CameraManager mCameraManager;
    private OnQRCodeReaderListener mOnQRCodeReaderListener;

    private int mPreviewWidth;
    private int mPreviewHeight;

    DecodeFrameTask decodeFrameTask;
    private Map<DecodeHintType, Object> decodeHints;

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

    public void setTorchEnabled(boolean state) {
        mCameraManager.setTorchEnabled(state);
    }

    public void setOnQRCodeReaderListener(OnQRCodeReaderListener onQRCodeReaderListener) {
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
        mPreviewWidth = mCameraManager.getPreviewSize().x;
        mPreviewHeight = mCameraManager.getPreviewSize().y;

        mCameraManager.stopPreview();
        mCameraManager.setPreviewCallback(this);
        mCameraManager.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraManager.setPreviewCallback(null);
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (decodeFrameTask != null
                && (decodeFrameTask.getStatus() == AsyncTask.Status.RUNNING
                || decodeFrameTask.getStatus() == AsyncTask.Status.PENDING)) {
            return;
        }

        decodeFrameTask = new DecodeFrameTask(this, decodeHints);
        decodeFrameTask.execute(bytes);
    }

    private static class DecodeFrameTask extends AsyncTask<byte[], Void, Result> {

        private final WeakReference<QRCodeReaderView> viewRef;
        private final WeakReference<Map<DecodeHintType, Object>> hintsRef;

        DecodeFrameTask(QRCodeReaderView view, Map<DecodeHintType, Object> hints) {
            viewRef = new WeakReference<>(view);
            hintsRef = new WeakReference<>(hints);
        }

        @Override
        protected Result doInBackground(byte[]... params) {
            final QRCodeReaderView view = viewRef.get();
            if (view == null) {
                return null;
            }

            final PlanarYUVLuminanceSource source =
                    view.mCameraManager.buildLuminanceSource(params[0], view.mPreviewWidth,
                            view.mPreviewHeight);

            final HybridBinarizer hybBin = new HybridBinarizer(source);
            final BinaryBitmap bitmap = new BinaryBitmap(hybBin);

            try {
                return view.mQRCodeReader.decode(bitmap, hintsRef.get());
            } catch (ChecksumException e) {

            } catch (NotFoundException e) {

            } catch (FormatException e) {

            } finally {
                view.mQRCodeReader.reset();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            final QRCodeReaderView view = viewRef.get();

            // Notify we found a QRCode
            if (view != null && result != null && view.mOnQRCodeReaderListener != null) {
                view.mOnQRCodeReaderListener.onSuccess(result);
            }
        }

    }
}
