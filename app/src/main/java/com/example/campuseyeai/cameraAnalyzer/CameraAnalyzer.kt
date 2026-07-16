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
    private val onRecognition: (List<RecognitionResult>) -> Unit,
    private val onError: (Exception) -> Unit = {}
) : ImageAnalysis.Analyzer {

    companion object {
        private const val COOLDOWN = 3000L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val isProcessing = AtomicBoolean(false)

    // Track last recognized students to avoid duplicate announcements
    private val recognizedCooldowns = mutableMapOf<String, Long>()

    override fun analyze(imageProxy: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        scope.launch {
            try {
                val results = recognizer.recognize(imageProxy)

                withContext(Dispatchers.Main) {
                    val newResults = results.filter { shouldNotify(it) }
                    if (newResults.isNotEmpty()) {
                        onRecognition(newResults)
                        for (result in newResults) {
                            if (result.recognized && result.admissionNo != null) {
                                recognizedCooldowns[result.admissionNo] = System.currentTimeMillis()
                            }
                        }
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
                
                // Cleanup old cooldowns occasionally
                cleanupCooldowns()
            }
        }
    }

    private fun shouldNotify(result: RecognitionResult): Boolean {
        val currentTime = System.currentTimeMillis()
        
        if (result.recognized && result.admissionNo != null) {
            val lastRecognitionTime = recognizedCooldowns[result.admissionNo] ?: 0L
            return (currentTime - lastRecognitionTime) >= COOLDOWN
        } else {
            // Cooldown for unknown faces using trackingId
            val key = "unknown_${result.trackingId ?: "none"}"
            val lastRecognitionTime = recognizedCooldowns[key] ?: 0L
            if ((currentTime - lastRecognitionTime) >= COOLDOWN) {
                recognizedCooldowns[key] = currentTime
                return true
            }
            return false
        }
    }

    private fun cleanupCooldowns() {
        val currentTime = System.currentTimeMillis()
        val iterator = recognizedCooldowns.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (currentTime - entry.value > COOLDOWN * 2) {
                iterator.remove()
            }
        }
    }

    fun shutdown() {
        scope.cancel()
    }
}