package com.example.campuseyeai.aiAnalyzer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorHelper {

    companion object {
        private const val TAG = "FaceDetectorHelper"
    }

    private val detector: FaceDetector by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setMinFaceSize(0.20f)
                .enableTracking()
                .build()
        )
    }

    @OptIn(ExperimentalGetImage::class)
    @SuppressLint("UnsafeOptInUsageError")
    fun detectFaces(imageProxy: ImageProxy): List<Face> {
        return try {
            val mediaImage = imageProxy.image
            if (mediaImage == null) {
                Log.w(TAG, "MediaImage is null")
                return emptyList()
            }

            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            val faces = Tasks.await(detector.process(inputImage))
            Log.d(TAG, "Detected ${faces.size} face(s)")
            faces
        } catch (e: Exception) {
            Log.e(TAG, "Face detection failed: ${e.message}", e)
            emptyList()
        }
    }

    fun close() {
        try {
            detector.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing detector: ${e.message}", e)
        }
    }
}