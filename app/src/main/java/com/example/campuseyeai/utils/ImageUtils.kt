package com.example.campuseyeai.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun toBitmap(
        image: ImageProxy
    ): Bitmap {

        val buffer =
            image.planes[0].buffer

        val bytes =
            ByteArray(buffer.remaining())

        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )

    }

}