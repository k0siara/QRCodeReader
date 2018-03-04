package com.patrykkosieradzki.qrcodereader.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public boolean isAnonymous;
    public int qrCodes;

    public User() {

    }

    public User(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        this.qrCodes = 0;
    }


}
