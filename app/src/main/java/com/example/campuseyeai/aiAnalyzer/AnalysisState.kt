package com.example.campuseyeai.aiAnalyzer

import java.util.concurrent.atomic.AtomicBoolean

object AnalysisState {

    private val processing = AtomicBoolean(false)

    fun tryAcquire(): Boolean {
        return processing.compareAndSet(false, true)
    }

    fun release() {
        processing.set(false)
    }

    fun isProcessing(): Boolean {
        return processing.get()
    }
}