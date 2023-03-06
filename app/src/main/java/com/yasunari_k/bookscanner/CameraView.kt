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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun CameraView(
    onImageCaptured: () -> Unit,
) {
    val context = LocalContext.current
    val cameraController = LifecycleCameraController(context)
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

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
                previewView.setOnTouchListener { _, _ -> false } //no-op
                return@MlKitAnalyzer
            }

            val qrCodeViewModel = BarcodeViewModel(barcodeResults[0])
            previewView.setOnTouchListener(qrCodeViewModel.barcodeCodeTouchCallback)
            previewView.overlay.clear()

            //if (processingBarcode.compareAndSet(false, true)) {
                beep()
                Log.d("dd--", "Result: ${barcodeResults[0]}")

                barcodeResults.clear()
                barcodeScanner.close()

                onImageCaptured.invoke()
            //}
        }
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
    }
}

private fun beep() {
    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
}