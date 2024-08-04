package com.example.todoapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Tasks(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "taskId")
    val id: String,

    @ColumnInfo(name = "taskTitle")
    val title: String,

    @ColumnInfo(name = "taskDescription")
    val description: String,

    @ColumnInfo(name = "taskDate")
    val date: Date
)
