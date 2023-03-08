package com.yasunari_k.bookscanner

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * A ViewModel for encapsulating the data for a QR Code, including the encoded data, the bounding
 * box, and the touch behavior on the QR Code.
 *
 * As is, this class only handles displaying the QR Code data if it's a URL. Other data types
 * can be handled by adding more cases of Barcode.TYPE_URL in the init block.
 */
class BarcodeViewModel(barcode: Barcode) {
    var boundingRect: Rect = barcode.boundingBox!!
    var barcodeContent: String = ""
    var barcodeCodeTouchCallback = { v: View, e: MotionEvent -> false} //no-op
    val tag = "BarcodeViewModel"

    init {
        when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                barcodeContent = barcode.url!!.url!!
                consoleLog("type URL: $barcodeContent")
                barcodeCodeTouchCallback = { v: View, e: MotionEvent ->
                    if (e.action == MotionEvent.ACTION_DOWN && boundingRect.contains(e.getX().toInt(), e.getY().toInt())) {
                        val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                        openBrowserIntent.data = Uri.parse(barcodeContent)
                        v.context.startActivity(openBrowserIntent)
                    }
                    true // return true from the callback to signify the event was handled
                }
            }
            // Add other QR Code types here to handle other types of data,
            // like Wifi credentials.
            Barcode.TYPE_ISBN -> {
                barcodeContent = "Data type: ISBN ${barcode.rawValue.toString()}"
                println("barcodeContent = $barcodeContent")
                consoleLog("type ISBN: $barcodeContent")
//                val isbnContent = barcode.url!!.url!!
//                qrCodeTouchCallback = { v: View, e: MotionEvent ->
//                    if (e.action == MotionEvent.ACTION_DOWN && boundingRect.contains(e.getX().toInt(), e.getY().toInt())) {
//                        val openBrowserIntent = Intent(Intent.ACTION_VIEW)
//                        openBrowserIntent.data = Uri.parse(isbnContent)
//                        v.context.startActivity(openBrowserIntent)
//                    }
//                    true // return true from the callback to signify the event was handled
//                }
            }
            else -> {
                barcodeContent = "Unsupported data type: ${barcode.rawValue.toString()}"
                System.out.println("barcodeContent = $barcodeContent")
                consoleLog("Unsupported type: $barcodeContent")
            }
        }
    }

    private fun consoleLog(message: String) {
        Log.i(tag, message)
    }
}