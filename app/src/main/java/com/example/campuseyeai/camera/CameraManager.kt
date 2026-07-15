package com.example.campuseyeai.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {

    val imageCapture = ImageCapture.Builder().build()

    fun startCamera(
        previewView: PreviewView,
        analyzer: ImageAnalysis.Analyzer
    ) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            try {

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()

                preview.surfaceProvider =
                    previewView.surfaceProvider

                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    analyzer
                )

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalysis
                )

            } catch (e: Exception) {

                Log.e(
                    "CameraManager",
                    "Failed to start camera",
                    e
                )

            }

        }, ContextCompat.getMainExecutor(context))

    }

}