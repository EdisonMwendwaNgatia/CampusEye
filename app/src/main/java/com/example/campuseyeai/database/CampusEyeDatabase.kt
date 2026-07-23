package com.example.campuseyeai.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Student::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CampusEyeDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        @Volatile
        private var INSTANCE: CampusEyeDatabase? = null

        fun getDatabase(
            context: Context
        ): CampusEyeDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusEyeDatabase::class.java,
                    "campus_eye.db"
                )
                    .fallbackToDestructiveMigration() // <-- Add this
                    .build()

                INSTANCE = instance

                instance

            }

        }

    }

}