package com.patrykkosieradzki.qrcodereader.utils


import java.text.SimpleDateFormat
import java.util.Calendar

object DateUtils {

    val currentDateAsString: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance()
                        .time)

}
