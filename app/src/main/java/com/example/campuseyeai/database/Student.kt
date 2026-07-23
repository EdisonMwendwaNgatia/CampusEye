package com.example.campuseyeai.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(

    @PrimaryKey
    val admissionNo: String,

    val fullName: String,

    val className: String,

    val imageFolder: String,

    val centerEmbedding: String,

    val leftEmbedding: String,

    val rightEmbedding: String,

    val isVisitor: Boolean = false
)