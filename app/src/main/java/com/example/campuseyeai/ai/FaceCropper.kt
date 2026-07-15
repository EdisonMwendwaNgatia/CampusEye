package com.example.campuseyeai.ai

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.core.graphics.scale
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceCropper {

    private val detector by lazy {

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
            )
            .setLandmarkMode(
                FaceDetectorOptions.LANDMARK_MODE_ALL
            )
            .setClassificationMode(
                FaceDetectorOptions.CLASSIFICATION_MODE_NONE
            )
            .enableTracking()
            .build()

        FaceDetection.getClient(options)
    }

    fun cropFace(bitmap: Bitmap): Bitmap? {

        val image =
            InputImage.fromBitmap(bitmap, 0)

        val result =
            Tasks.await(detector.process(image))

        if (result.isEmpty())
            return null

        val face = result.first()

        val rect =
            expandRect(
                face.boundingBox,
                bitmap.width,
                bitmap.height
            )

        val cropped =
            Bitmap.createBitmap(
                bitmap,
                rect.left,
                rect.top,
                rect.width(),
                rect.height()
            )

        return cropped.scale(
            160,
            160
        )
    }

    private fun expandRect(
        rect: Rect,
        width: Int,
        height: Int
    ): Rect {

        val paddingX =
            (rect.width() * 0.25f).toInt()

        val paddingY =
            (rect.height() * 0.35f).toInt()

        val left =
            (rect.left - paddingX)
                .coerceAtLeast(0)

        val top =
            (rect.top - paddingY)
                .coerceAtLeast(0)

        val right =
            (rect.right + paddingX)
                .coerceAtMost(width)

        val bottom =
            (rect.bottom + paddingY)
                .coerceAtMost(height)

        return Rect(
            left,
            top,
            right,
            bottom
        )
    }

    fun close() {
        detector.close()
    }
}