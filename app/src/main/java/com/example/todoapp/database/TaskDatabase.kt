package com.example.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.converter.TypeConverter
import com.example.todoapp.models.Tasks

@Database(
    entities = [Tasks::class],
    version = 1
)

@TypeConverters(TypeConverter::class)
abstract class TaskDatabase: RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE : TaskDatabase? = null
        fun getInstance(context: Context) : TaskDatabase{
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}