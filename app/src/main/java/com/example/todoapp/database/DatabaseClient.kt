package com.example.todoapp.database

import android.content.Context
import androidx.room.Room.databaseBuilder

class DatabaseClient private constructor(mCtx: Context) {
    //our app database object
    private val appDatabase: AppDatabase = databaseBuilder(mCtx, AppDatabase::class.java, "Task.db")
        .fallbackToDestructiveMigration()
        .build()

    fun getAppDatabase(): AppDatabase {
        return appDatabase
    }

    companion object {
        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance
        }
    }
}