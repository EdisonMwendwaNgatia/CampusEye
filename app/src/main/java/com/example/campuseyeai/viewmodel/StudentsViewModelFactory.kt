package com.example.campuseyeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.campuseyeai.repository.StudentRepository

class StudentsViewModelFactory(
    private val repository: StudentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(StudentsViewModel::class.java)) {

            return StudentsViewModel(repository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel")

    }

}