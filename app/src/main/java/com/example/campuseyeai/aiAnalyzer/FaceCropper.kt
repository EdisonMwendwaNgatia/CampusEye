package com.example.campuseyeai.aiAnalyzer


import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import kotlin.math.max
import kotlin.math.min

object FaceCropper {

    private const val OUTPUT_SIZE = 160

    /**
     * Crops a detected face and resizes it for FaceNet.
     */
    fun cropFace(
        bitmap: Bitmap,
        face: Face
    ): Bitmap {

        val rect = expandRect(
            face.boundingBox,
            bitmap.width,
            bitmap.height,
            0.30f
        )

        val cropped = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )

        return Bitmap.createScaledBitmap(
            cropped,
            OUTPUT_SIZE,
            OUTPUT_SIZE,
            true
        )

    }

    /**
     * Expands the detected face so FaceNet sees
     * forehead, chin and cheeks.
     */
    private fun expandRect(
        rect: Rect,
        imageWidth: Int,
        imageHeight: Int,
        percent: Float
    ): Rect {

        val dx = (rect.width() * percent).toInt()
        val dy = (rect.height() * percent).toInt()

        return Rect(

            max(0, rect.left - dx),

            max(0, rect.top - dy),

            min(imageWidth, rect.right + dx),

            min(imageHeight, rect.bottom + dy)

        )

    }

}