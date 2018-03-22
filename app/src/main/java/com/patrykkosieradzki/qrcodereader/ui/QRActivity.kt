package com.patrykkosieradzki.qrcodereader.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.Result
import com.google.zxing.client.result.ResultParser
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity
import com.patrykkosieradzki.qrcodereader.utils.DateUtils
import com.patrykkosieradzki.qrcodereader.utils.DeviceUtils

import butterknife.ButterKnife
import butterknife.OnClick
import com.patrykkosieradzki.qrcodereader.extensions.hasPermission
import com.patrykkosieradzki.qrcodereader.extensions.requestPermission
import kotlinx.android.synthetic.main.activity_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    companion object {
        private val REQUEST_CAMERA = 0 // TODO: move request to home activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        checkCameraPermission()
        initScannerView()

        layout_flashlight.setOnClickListener { onFlashlightLayoutClick() }
    }

    private fun checkCameraPermission() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA)
        }
    }

    private fun initScannerView() {
        mScannerView.apply {
            setSquareViewFinder(true)
            setResultHandler { this }
            setLaserEnabled(false)
            setAutoFocus(true)
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScannerView()
            } else {
                // TODO: Handle retarded user
            }
        }
    }

    fun onFlashlightLayoutClick() { // TODO: kotlin it hard
        if (flashlight.drawable.constantState === resources.getDrawable(R.drawable.flashlight_off).constantState) {
            flashlight.setImageResource(R.drawable.flashlight_on)
            mScannerView.flash = true
        } else {
            flashlight.setImageResource(R.drawable.flashlight_off)
            mScannerView.flash = false
        }
    }

    override fun handleResult(result: Result) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200L)

        val text = result.text
        val type = ResultParser.parseResult(result).type.name
        val qrCode = QRCode(text, type, DateUtils.currentDateAsString)

        val intent = Intent()
        intent.putExtra("qrCode", qrCode)
        setResult(HomeActivity.QR_READ, intent)
        finish()
    }

    public override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }



}