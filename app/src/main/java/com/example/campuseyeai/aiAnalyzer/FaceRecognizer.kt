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
    ): List<RecognitionResult> {
        return try {
            // Step 1: Detect faces
            val faces = faceDetector.detectFaces(imageProxy)
            if (faces.isEmpty()) {
                return emptyList()
            }

            // Step 2: Convert ImageProxy to Bitmap
            val bitmap = ImageProxyBitmapConverter.toBitmap(imageProxy)

            val results = mutableListOf<RecognitionResult>()
            val students = repository.getAllStudents()

            // Step 3: Process each detected face
            for (face in faces) {
                // Crop face
                val croppedFace = FaceCropper.cropFace(bitmap, face)

                // Generate embedding
                val embedding = embeddingGenerator.generateEmbedding(croppedFace)
                    ?: continue

                // Compare with database
                if (students.isEmpty()) {
                    results.add(RecognitionResult(recognized = false, similarity = 0f))
                    continue
                }

                var bestStudent: Student? = null
                var bestScore = -1f

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

                    if (bestScore >= 0.95f) break
                }

                if (bestStudent == null || bestScore < MATCH_THRESHOLD) {
                    results.add(
                        RecognitionResult(
                            recognized = false,
                            similarity = bestScore,
                            boundingBox = face.boundingBox,
                            trackingId = face.trackingId
                        )
                    )
                } else {
                    results.add(
                        RecognitionResult(
                            recognized = true,
                            studentName = bestStudent.fullName,
                            admissionNo = bestStudent.admissionNo,
                            similarity = bestScore,
                            boundingBox = face.boundingBox,
                            trackingId = face.trackingId,
                            isVisitor = bestStudent.isVisitor
                        )
                    )
                }
            }
            results

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun close() {
        faceDetector.close()
        embeddingGenerator.close()
    }
}