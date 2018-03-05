package com.patrykkosieradzki.qrcodereader.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static String getCurrentDateAsString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance()
                        .getTime());
    }

}
