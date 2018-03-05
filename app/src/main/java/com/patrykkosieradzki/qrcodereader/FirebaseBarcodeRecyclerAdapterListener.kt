package com.patrykkosieradzki.qrcodereader

import com.patrykkosieradzki.qrcodereader.model.QRCode


interface FirebaseBarcodeRecyclerAdapterListener {
    fun onIconClicked(model: QRCode, position: Int)
    fun onIconImportantClicked(model: QRCode, position: Int)
    fun onMessageRowClicked(model: QRCode, position: Int)
    fun onRowLongClicked(model: QRCode, position: Int)
}