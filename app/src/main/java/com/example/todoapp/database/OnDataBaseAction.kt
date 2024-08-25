package com.example.todoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todoapp.model.Task

@Dao
interface OnDataBaseAction {
    @Query("SELECT * FROM Task")
    fun getAllTasksList(): List<Task>  // No need for nullable type here

    @Query("DELETE FROM Task")
    fun truncateTheList()

    @Insert
    fun insertDataIntoTaskList(task: Task)  // Non-nullable parameter

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    fun deleteTaskFromId(taskId: Int)

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    fun selectDataFromAnId(taskId: Int): Task?  // This can remain nullable if the task might not be found

    @Query(
        "UPDATE Task SET taskTitle = :taskTitle, taskDescription = :taskDescription, date = :taskDate, " +
                "lastAlarm = :taskTime, event = :taskEvent WHERE taskId = :taskId"
    )
    fun updateAnExistingRow(
        taskId: Int,
        taskTitle: String?,
        taskDescription: String?,
        taskDate: String?,
        taskTime: String?,
        taskEvent: String?
    )  // Make sure to handle nullability in your business logic if needed
}
