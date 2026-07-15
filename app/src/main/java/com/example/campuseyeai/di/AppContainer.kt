package com.example.campuseyeai.di

import android.content.Context
import com.example.campuseyeai.database.CampusEyeDatabase
import com.example.campuseyeai.repository.StudentRepository

object AppContainer {

    lateinit var repository: StudentRepository

    fun initialize(
        context: Context
    ) {

        val db = CampusEyeDatabase.getDatabase(context)

        repository = StudentRepository(
            db.studentDao()
        )

    }

}