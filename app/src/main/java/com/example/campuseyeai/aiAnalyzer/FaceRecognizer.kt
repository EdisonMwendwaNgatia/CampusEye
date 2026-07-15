package com.example.campuseyeai.aiAnalyzer

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.example.campuseyeai.ai.EmbeddingUtils
import com.example.campuseyeai.ai.FaceEmbeddingGenerator
import com.example.campuseyeai.database.Student
import com.example.campuseyeai.repository.StudentRepository
import kotlin.math.max

class FaceRecognizer(
    context: Context,
    private val repository: StudentRepository
) {

    companion object {
        /**
         * Adjust later after testing.
         *
         * 0.60 = Loose
         * 0.70 = Recommended
         * 0.80 = Strict
         */
        private const val MATCH_THRESHOLD = 0.70f
    }

    private val faceDetector = FaceDetectorHelper()
    private val embeddingGenerator = FaceEmbeddingGenerator(context)

    suspend fun recognize(
        imageProxy: ImageProxy
    ): RecognitionResult? {
        return try {
            // Step 1: Detect faces
            val faces = faceDetector.detectFaces(imageProxy)
            if (faces.isEmpty()) {
                // ❌ REMOVED: imageProxy.close()
                return null
            }

            // Step 2: Convert ImageProxy to Bitmap
            val bitmap = ImageProxyBitmapConverter.toBitmap(imageProxy)

            // Step 3: Crop the first detected face
            val croppedFace = FaceCropper.cropFace(bitmap, faces.first())

            // Step 4: Generate embedding
            val embedding = embeddingGenerator.generateEmbedding(croppedFace)
                ?: return null

            // Step 5: Compare with database
            val students = repository.getAllStudents()
            if (students.isEmpty()) {
                return null
            }

            var bestStudent: Student? = null
            var bestScore = -1f

            // ✅ Improvement: Use for loop instead of forEach
            for (student in students) {
                val center = EmbeddingUtils.fromJson(student.centerEmbedding)
                val left = EmbeddingUtils.fromJson(student.leftEmbedding)
                val right = EmbeddingUtils.fromJson(student.rightEmbedding)

                val centerScore = CosineSimilarity.calculate(embedding, center)
                val leftScore = CosineSimilarity.calculate(embedding, left)
                val rightScore = CosineSimilarity.calculate(embedding, right)

                val score = max(centerScore, max(leftScore, rightScore))

                if (score > bestScore) {
                    bestScore = score
                    bestStudent = student
                }

                // Optional: Early break if confidence is very high
                if (bestScore >= 0.95f) {
                    break
                }
            }

            if (bestStudent == null || bestScore < MATCH_THRESHOLD) {
                return RecognitionResult(
                    recognized = false,
                    similarity = bestScore
                )
            }

            RecognitionResult(
                recognized = true,
                studentName = bestStudent.fullName,
                admissionNo = bestStudent.admissionNo,
                similarity = bestScore
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        // ❌ REMOVED: No close here either - only CameraAnalyzer should close it
    }

    fun close() {
        faceDetector.close()
        embeddingGenerator.close()
    }
}