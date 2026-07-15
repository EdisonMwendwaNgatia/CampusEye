package com.example.campuseyeai.aiAnalyzer

object MatchedFaceCooldown {

    private const val MATCH_DELAY = 2500L

    private var lastMatch = 0L

    fun canRecognizeAgain(): Boolean {
        return System.currentTimeMillis() - lastMatch >= MATCH_DELAY
    }

    fun onMatched() {
        lastMatch = System.currentTimeMillis()
    }
}