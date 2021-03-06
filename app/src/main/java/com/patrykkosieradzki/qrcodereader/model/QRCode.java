package com.patrykkosieradzki.qrcodereader.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class QRCode implements Serializable {

    public String text;
    public String type;
    public String createdAt;

    public QRCode(String text, String type, String createdAt) {
        this.text = text;
        this.type = type;
        this.createdAt = createdAt;
    }

    public QRCode() {}
}