package com.example.campuseyeai.cameraAnalyzer

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.campuseyeai.aiAnalyzer.FaceRecognizer
import com.example.campuseyeai.aiAnalyzer.RecognitionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class CameraAnalyzer(
    private val recognizer: FaceRecognizer,
    private val onRecognition: (RecognitionResult) -> Unit,
    private val onError: (Exception) -> Unit = {}
) : ImageAnalysis.Analyzer {

    companion object {
        private const val COOLDOWN = 3000L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val isProcessing = AtomicBoolean(false)

    // Track last recognized student to avoid duplicate announcements
    private var lastRecognizedId: String? = null
    private var lastRecognitionTime: Long = 0L

    override fun analyze(imageProxy: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        scope.launch {
            try {
                val result = recognizer.recognize(imageProxy)

                withContext(Dispatchers.Main) {
                    // Handle nullable result
                    if (result != null) {
                        // Suppress duplicate recognition within cooldown
                        if (shouldNotify(result)) {
                            onRecognition(result)
                            if (result.recognized) {
                                lastRecognizedId = result.admissionNo
                                lastRecognitionTime = System.currentTimeMillis()
                            }
                        }
                    } else {
                        // If result is null, we can optionally notify about failure
                        // or just ignore it
                        // onRecognition(RecognitionResult(recognized = false))
                    }
                }

                delay(COOLDOWN)

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                // Close the image proxy and reset processing flag
                imageProxy.close()
                isProcessing.set(false)
            }
        }
    }

    private fun shouldNotify(result: RecognitionResult): Boolean {
        if (!result.recognized) {
            return true // Always notify unknown
        }

        val currentTime = System.currentTimeMillis()
        val isSameStudent = result.admissionNo == lastRecognizedId
        val isWithinCooldown = (currentTime - lastRecognitionTime) < COOLDOWN

        // Only notify if it's a different student or cooldown has passed
        return !(isSameStudent && isWithinCooldown)
    }

    fun shutdown() {
        scope.cancel()
    }
}