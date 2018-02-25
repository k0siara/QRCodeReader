package com.patrykkosieradzki.qrcodereader;

public interface OnQRCodeReaderListener {

    void onSuccess(String data);
    void onFailure();
}
