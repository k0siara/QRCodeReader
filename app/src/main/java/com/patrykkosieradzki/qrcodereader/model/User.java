package com.patrykkosieradzki.qrcodereader.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String createdAt;
    public Map<String, QRCode> qrCodes;

    public User(String createdAt) {
        this.createdAt = createdAt;
        this.qrCodes = new HashMap<>();
    }

    public User() {}

}
