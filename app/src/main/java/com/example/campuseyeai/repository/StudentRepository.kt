package com.example.campuseyeai.repository

import com.example.campuseyeai.database.Student
import com.example.campuseyeai.database.StudentDao
import kotlinx.coroutines.flow.Flow

class StudentRepository(

    private val dao: StudentDao

) {

    suspend fun insertStudent(
        student: Student
    ) {

        dao.insertStudent(student)

    }

    fun getStudents(): Flow<List<Student>> {

        return dao.getStudents()

    }

    suspend fun getAllStudents(): List<Student> {
        return dao.getAllStudents()
    }

    suspend fun deleteStudent(admissionNo: String) {
        dao.deleteStudent(admissionNo)
    }

    suspend fun updateEmbeddings(
        admissionNo: String,
        center: String,
        left: String,
        right: String
    ) {
        dao.updateEmbeddings(
            admissionNo,
            center,
            left,
            right
        )
    }

}