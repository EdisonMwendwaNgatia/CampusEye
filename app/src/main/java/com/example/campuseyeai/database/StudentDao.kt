package com.example.campuseyeai.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(
        student: Student
    )

    @Query("SELECT * FROM students")
    fun getStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>

    @Query("SELECT * FROM students WHERE admissionNo=:id")
    suspend fun getStudent(
        id: String
    ): Student?

    @Query("""
UPDATE students
SET centerEmbedding = :center,
    leftEmbedding = :left,
    rightEmbedding = :right
WHERE admissionNo = :admissionNo
""")
    suspend fun updateEmbeddings(
        admissionNo: String,
        center: String,
        left: String,
        right: String
    )

    @Query("DELETE FROM students")
    suspend fun deleteAll()
}