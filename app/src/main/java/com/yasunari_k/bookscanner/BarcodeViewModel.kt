package com.yasunari_k.bookscanner

import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * A ViewModel for encapsulating the data for a QR Code, including the encoded data, the bounding
 * box, and the touch behavior on the QR Code.
 *
 * As is, this class only handles displaying the QR Code data if it's a URL. Other data types
 * can be handled by adding more cases of Barcode.TYPE_URL in the init block.
 */
class BarcodeViewModel(barcode: Barcode) {
    var barcodeContent: String = ""
    private val tag = "BarcodeViewModel"

    init {
        barcodeContent = barcode.rawValue.toString()
        when (barcode.valueType) {
            Barcode.TYPE_TEXT -> consoleLog("TYPE_TEXT: $barcodeContent")
            Barcode.TYPE_ISBN -> consoleLog("type ISBN: $barcodeContent")
            else -> consoleLog("Unsupported type: $barcodeContent")
        }
    }

    private fun consoleLog(message: String) {
        Log.i(tag, message)
    }
}