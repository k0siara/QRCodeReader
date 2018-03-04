package com.patrykkosieradzki.qrcodereader;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class QRCode {

    public String title;
    public String description;
    public String type;

    public QRCode() {

    }

    public QRCode(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }
}
