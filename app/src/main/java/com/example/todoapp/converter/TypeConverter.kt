package com.example.todoapp.converter

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    fun fromTimestampToDate(value:Long): Date{
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date:Date): Long{
        return date.time
    }
}