package com.example.campuseyeai.aiAnalyzer

data class RecognitionResult(

    val recognized: Boolean,

    val studentName: String? = null,

    val admissionNo: String? = null,

    val similarity: Float = 0f

)