package com.example.campuseyeai.aiAnalyzer

import android.graphics.Rect

data class RecognitionResult(

    val recognized: Boolean,

    val studentName: String? = null,

    val admissionNo: String? = null,

    val similarity: Float = 0f,

    val boundingBox: Rect? = null,

    val trackingId: Int? = null,

    val isVisitor: Boolean = false

)