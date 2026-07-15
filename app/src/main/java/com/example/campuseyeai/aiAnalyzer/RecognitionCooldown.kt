package com.example.campuseyeai.aiAnalyzer

object RecognitionCooldown {

    // milliseconds
    private const val COOLDOWN = 700L

    private var lastRecognition = 0L

    fun canProcess(): Boolean {

        val now = System.currentTimeMillis()

        if (now - lastRecognition >= COOLDOWN) {
            lastRecognition = now
            return true
        }

        return false
    }
}