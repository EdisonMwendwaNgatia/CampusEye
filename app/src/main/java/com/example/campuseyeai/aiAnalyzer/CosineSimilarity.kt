package com.example.campuseyeai.aiAnalyzer

import kotlin.math.sqrt

object CosineSimilarity {

    fun calculate(

        first: FloatArray,
        second: FloatArray

    ): Float {

        var dot = 0f
        var normA = 0f
        var normB = 0f

        for (i in first.indices) {

            dot += first[i] * second[i]

            normA += first[i] * first[i]

            normB += second[i] * second[i]

        }

        return (dot / (sqrt(normA) * sqrt(normB)))

    }

}