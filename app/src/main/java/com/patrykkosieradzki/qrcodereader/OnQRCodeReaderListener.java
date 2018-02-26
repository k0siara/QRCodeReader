package com.patrykkosieradzki.qrcodereader;

import com.google.zxing.Result;

public interface OnQRCodeReaderListener {

    void onSuccess(Result result);
    void onFailure();
}
