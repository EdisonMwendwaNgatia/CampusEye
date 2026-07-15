package com.example.campuseyeai.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

class EnrollmentProcessor(
    context: Context
) {

    companion object {

        private const val TAG = "EnrollmentProcessor"
        private const val TARGET_SIZE = 160

    }

    private val generator = FaceEmbeddingGenerator(context)
    private val cropper = FaceCropper()

    fun close() {
        generator.close()
        cropper.close()
    }

    fun generateEmbeddings(
        folder: String
    ): Triple<String, String, String> {

        Log.d(TAG, "Reading images from: $folder")

        val centerBitmap = loadOptimizedBitmap(
            File(folder, "center.jpg")
        )

        val leftBitmap = loadOptimizedBitmap(
            File(folder, "left.jpg")
        )

        val rightBitmap = loadOptimizedBitmap(
            File(folder, "right.jpg")
        )

        try {

            Log.d(TAG, "Images loaded successfully")

            val centerFace =
                cropper.cropFace(centerBitmap)
                    ?: throw IllegalStateException(
                        "No face detected in center.jpg"
                    )

            val leftFace =
                cropper.cropFace(leftBitmap)
                    ?: throw IllegalStateException(
                        "No face detected in left.jpg"
                    )

            val rightFace =
                cropper.cropFace(rightBitmap)
                    ?: throw IllegalStateException(
                        "No face detected in right.jpg"
                    )

            Log.d(TAG, "Faces cropped successfully")

            val centerEmbedding =
                generator.generateEmbedding(centerFace)
                    ?: throw IllegalStateException(
                        "Center embedding failed"
                    )

            val leftEmbedding =
                generator.generateEmbedding(leftFace)
                    ?: throw IllegalStateException(
                        "Left embedding failed"
                    )

            val rightEmbedding =
                generator.generateEmbedding(rightFace)
                    ?: throw IllegalStateException(
                        "Right embedding failed"
                    )

            Log.d(TAG, "Embeddings generated")

            val centerJson =
                EmbeddingUtils.toJson(centerEmbedding)

            val leftJson =
                EmbeddingUtils.toJson(leftEmbedding)

            val rightJson =
                EmbeddingUtils.toJson(rightEmbedding)

            Log.d(TAG, "Embeddings converted to JSON")

            centerFace.recycle()
            leftFace.recycle()
            rightFace.recycle()

            return Triple(
                centerJson,
                leftJson,
                rightJson
            )

        } finally {

            centerBitmap.recycle()
            leftBitmap.recycle()
            rightBitmap.recycle()

            close()
        }
    }

    /**
     * Loads a smaller bitmap from disk to reduce memory usage.
     */
    private fun loadOptimizedBitmap(
        file: File
    ): Bitmap {

        if (!file.exists()) {
            throw IllegalStateException(
                "${file.name} not found"
            )
        }

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(
            file.absolutePath,
            options
        )

        options.inSampleSize =
            calculateInSampleSize(
                options,
                640,
                640
            )

        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        return BitmapFactory.decodeFile(
            file.absolutePath,
            options
        ) ?: throw IllegalStateException(
            "Failed to decode ${file.name}"
        )
    }

    /**
     * Calculates the best sampling factor.
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {

        val height = options.outHeight
        val width = options.outWidth

        var sampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (
                halfHeight / sampleSize >= reqHeight &&
                halfWidth / sampleSize >= reqWidth
            ) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }
}