package com.yasunari_k.bookscanner

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun ScanView(
    barcodeFormatToScan: Int,
    onImageCaptured: (String) -> Unit,
) {
    val context = LocalContext.current
    val cameraController = LifecycleCameraController(context)
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(barcodeFormatToScan)
        .build()
    consoleLog("Barcode Format: $barcodeFormatToScan")

    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(context)
        ) { result: MlKitAnalyzer.Result? ->
            val barcodeResults = result?.getValue(barcodeScanner)
            if ((barcodeResults == null) ||
                (barcodeResults.size == 0) ||
                (barcodeResults.first() == null)
            ) {
                previewView.overlay.clear()
                return@MlKitAnalyzer
            }

            val qrCodeViewModel = BarcodeViewModel(barcodeResults[0])
            previewView.overlay.clear()

            beep()

            barcodeResults.clear()
            barcodeScanner.close()

            when(barcodeFormatToScan) {
                Barcode.FORMAT_QR_CODE -> {
                    onImageCaptured.invoke(qrCodeViewModel.barcodeContent)
                }
                Barcode.FORMAT_EAN_13 -> {
                    val isbnCode = qrCodeViewModel.barcodeContent
                    onImageCaptured.invoke(isbnCode)
                }
                else -> {
                    consoleLog("Unknown... (^^;)")
                    //TODO: do something to get out of the scan view
                }
            }
        }
    )

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                consoleLog("on start")
            } else if (event == Lifecycle.Event.ON_STOP) {
                consoleLog("on stop")
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            barcodeScanner.close()//test
            consoleLog("The effect is leaving the Composition")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController

    CameraView(previewView = previewView)
}

@Composable
fun CameraView(previewView: PreviewView) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Camera View" }
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
    }
}

private fun beep() {
    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
}

private fun consoleLog(messageToLog: String) {
    Log.i("CameraView.kt", messageToLog)
}