package com.example.campuseyeai.ai

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

class FaceEmbeddingGenerator(
    context: Context
) {

    companion object {
        private const val TAG = "FaceEmbeddingGenerator"
        private const val INPUT_SIZE = 160        // FaceNet expects 160x160
        private const val EMBEDDING_SIZE = 128    // FaceNet outputs 128-d embeddings
    }

    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(
            loadModelFile(
                context,
                "facenet.tflite"
            )
        )

        // Log model input and output details
        val inputTensor = interpreter.getInputTensor(0)
        val outputTensor = interpreter.getOutputTensor(0)

        Log.d(TAG, "Input Shape: ${inputTensor.shape().contentToString()}")
        Log.d(TAG, "Input Type: ${inputTensor.dataType()}")
        Log.d(TAG, "Output Shape: ${outputTensor.shape().contentToString()}")
        Log.d(TAG, "Output Type: ${outputTensor.dataType()}")
    }

    private fun loadModelFile(
        context: Context,
        modelName: String
    ): MappedByteBuffer {

        val fileDescriptor = context.assets.openFd(modelName)

        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->

            val fileChannel = inputStream.channel

            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )

        }
    }

    fun generateEmbedding(bitmap: Bitmap): FloatArray? {
        return try {
            val resized = Bitmap.createScaledBitmap(
                bitmap,
                INPUT_SIZE,
                INPUT_SIZE,
                true
            )

            val input = convertBitmap(resized)

            val output = Array(1) { FloatArray(EMBEDDING_SIZE) }

            interpreter.run(input, output)

            output[0]
        } catch (e: Exception) {
            Log.e(TAG, "Error generating embedding: ${e.message}", e)
            null
        }
    }

    private fun convertBitmap(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(
            INPUT_SIZE * INPUT_SIZE * 3 * 4
        )
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            buffer.putFloat((r - 127.5f) / 128f)
            buffer.putFloat((g - 127.5f) / 128f)
            buffer.putFloat((b - 127.5f) / 128f)
        }

        buffer.rewind()
        return buffer
    }

    fun close() {
        interpreter.close()
    }

    fun isModelLoaded(): Boolean {
        return try {
            interpreter.getInputTensor(0) != null
        } catch (e: Exception) {
            false
        }
    }

    // 🔑 Cosine similarity helper
    fun cosineSimilarity(vec1: FloatArray, vec2: FloatArray): Float {
        require(vec1.size == vec2.size) { "Vectors must be same length" }

        var dot = 0f
        var normA = 0f
        var normB = 0f

        for (i in vec1.indices) {
            dot += vec1[i] * vec2[i]
            normA += vec1[i] * vec1[i]
            normB += vec2[i] * vec2[i]
        }

        return dot / (sqrt(normA.toDouble()) * sqrt(normB.toDouble())).toFloat()
    }
}
