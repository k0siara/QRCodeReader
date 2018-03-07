package com.patrykkosieradzki.qrcodereader.logger

import android.util.Log
import com.patrykkosieradzki.qrcodereader.extensions.getClassName

object Logger {
    var enabled: Boolean = false

    // TODO: finish logger by extending T: Any (maybe)
    fun log(func: () -> String) {
        Log.d(getClassName(), func())
    }
}
