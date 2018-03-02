package com.patrykkosieradzki.qrcodereader;

public class QRCode {

    private String text;

    public QRCode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
