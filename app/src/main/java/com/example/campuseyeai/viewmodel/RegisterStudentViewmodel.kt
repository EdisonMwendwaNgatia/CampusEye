package com.example.campuseyeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuseyeai.database.Student
import com.example.campuseyeai.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterStudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    fun registerStudent(
        admissionNo: String,
        fullName: String,
        className: String,
        imageFolder: String
    ) {

        if (
            admissionNo.isBlank() ||
            fullName.isBlank() ||
            className.isBlank()
        ) {

            _message.value = "Please fill all fields."
            _registrationSuccess.value = false
            return

        }

        _isSaving.value = true
        _message.value = ""
        _registrationSuccess.value = false

        viewModelScope.launch {

            try {

                val student = Student(

                    admissionNo = admissionNo,

                    fullName = fullName,

                    className = className,

                    imageFolder = imageFolder,

                    centerEmbedding = "",

                    leftEmbedding = "",

                    rightEmbedding = ""

                )

                repository.insertStudent(student)

                _message.value = "Student registered successfully."
                _registrationSuccess.value = true

            } catch (e: Exception) {

                _message.value = e.localizedMessage ?: "Registration failed."
                _registrationSuccess.value = false

            }

            _isSaving.value = false

        }

    }

    fun resetRegistrationSuccess() {

        _registrationSuccess.value = false

    }

    fun clearMessage() {

        _message.value = ""

    }

}