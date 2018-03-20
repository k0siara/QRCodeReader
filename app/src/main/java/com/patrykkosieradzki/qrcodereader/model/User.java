package com.patrykkosieradzki.qrcodereader.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.patrykkosieradzki.qrcodereader.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    @Exclude
    private String uid;

    public String createdAt;
    public Map<String, QRCode> qrCodes;

    public User(String uid) {
        this.uid = uid;
        this.createdAt = DateUtils.INSTANCE.getCurrentDateAsString();
        this.qrCodes = new HashMap<>();
    }

    public User() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}