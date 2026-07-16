package com.example.campuseyeai.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuseyeai.ai.EnrollmentProcessor
import com.example.campuseyeai.database.Student
import com.example.campuseyeai.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentsViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    val students = repository.getStudents()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // Reuse the FaceNet model
    private var enrollmentProcessor: EnrollmentProcessor? = null

    fun generateEmbeddings(
        context: Context,
        student: Student
    ) {

        // Prevent multiple clicks
        if (_loading.value) return

        viewModelScope.launch {

            _loading.value = true
            _message.value = ""

            try {

                val processor = enrollmentProcessor
                    ?: EnrollmentProcessor(context.applicationContext).also {
                        enrollmentProcessor = it
                    }

                val embeddings = withContext(Dispatchers.IO) {

                    processor.generateEmbeddings(
                        student.imageFolder
                    )

                }

                withContext(Dispatchers.IO) {

                    repository.updateEmbeddings(
                        student.admissionNo,
                        embeddings.first,
                        embeddings.second,
                        embeddings.third
                    )

                }

                Log.d(
                    "StudentsViewModel",
                    "Center Embedding: ${embeddings.first.take(100)}"
                )

                Log.d(
                    "StudentsViewModel",
                    "Embeddings saved for ${student.fullName}"
                )

                _message.value = "Embeddings generated successfully."

            } catch (e: Exception) {

                Log.e(
                    "StudentsViewModel",
                    "Embedding generation failed",
                    e
                )

                _message.value =
                    e.message ?: "Embedding generation failed."

            }

            _loading.value = false

        }

    }

    fun deleteStudent(admissionNo: String) {
        viewModelScope.launch {
            repository.deleteStudent(admissionNo)
        }
    }

    override fun onCleared() {
        super.onCleared()

        enrollmentProcessor?.close()
    }

}