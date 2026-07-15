package com.example.campuseyeai.aiAnalyzer

import android.graphics.*
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

object ImageProxyBitmapConverter {

    fun toBitmap(
        image: ImageProxy
    ): Bitmap {

        val nv21 = yuv420888ToNv21(image)

        val yuvImage = YuvImage(
            nv21,
            ImageFormat.NV21,
            image.width,
            image.height,
            null
        )

        val out = ByteArrayOutputStream()

        yuvImage.compressToJpeg(
            Rect(
                0,
                0,
                image.width,
                image.height
            ),
            100,
            out
        )

        val bytes = out.toByteArray()

        var bitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )

        bitmap = rotateBitmap(
            bitmap,
            image.imageInfo.rotationDegrees.toFloat()
        )

        return bitmap
    }

    private fun rotateBitmap(
        bitmap: Bitmap,
        rotation: Float
    ): Bitmap {

        if (rotation == 0f)
            return bitmap

        val matrix = Matrix()

        matrix.postRotate(rotation)

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun yuv420888ToNv21(
        image: ImageProxy
    ): ByteArray {

        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(
            ySize + uSize + vSize
        )

        yBuffer.get(
            nv21,
            0,
            ySize
        )

        vBuffer.get(
            nv21,
            ySize,
            vSize
        )

        uBuffer.get(
            nv21,
            ySize + vSize,
            uSize
        )

        return nv21
    }

}