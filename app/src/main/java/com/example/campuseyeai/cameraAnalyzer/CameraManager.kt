package com.example.campuseyeai.cameraAnalyzer

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context
) {

    // Fix 2: Create background executor for heavy processing
    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        analyzer: ImageAnalysis.Analyzer
    ) {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        // Fix 1: Use main executor for camera binding (UI thread)
        cameraProviderFuture.addListener({
            val cameraProvider =
                cameraProviderFuture.get()

            val preview =
                Preview.Builder()
                    .build()

            preview.surfaceProvider =
                previewView.surfaceProvider

            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

            // Fix 2: Use background executor for analysis
            imageAnalysis.setAnalyzer(
                cameraExecutor,  // Changed from main executor
                analyzer
            )

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageAnalysis
            )

            // Fix 1: Main executor for UI thread callbacks
        }, ContextCompat.getMainExecutor(context))
    }


    fun shutdown() {
        cameraExecutor.shutdown()
    }
}