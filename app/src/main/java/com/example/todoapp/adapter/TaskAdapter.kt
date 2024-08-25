package com.example.todoapp.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.activity.MainActivity
import com.example.todoapp.bottomSheetFragment.CreateTaskBottomSheetFragment
import com.example.todoapp.database.DatabaseClient
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val context: MainActivity,
    private var taskList: MutableList<Task>,
    var setRefreshListener: CreateTaskBottomSheetFragment.SetRefreshListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)
    private val inputDateFormat = SimpleDateFormat("dd-M-yyyy", Locale.US)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(inflater, viewGroup, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun showPopUpMenu(view: View, position: Int) {
        val task = taskList[position]
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                        .setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.sureToDelete)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            deleteTaskFromId(task.taskId, position)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
                        .show()
                }

                R.id.menuUpdate -> {
                    val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
                    createTaskBottomSheetFragment.setTaskId(task.taskId, true, context, context)
                    createTaskBottomSheetFragment.show(
                        context.supportFragmentManager,
                        createTaskBottomSheetFragment.tag
                    )
                }

                R.id.menuComplete -> {
                    AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.sureToMarkAsComplete)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            showCompleteDialog(task.taskId, position)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
                        .show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showCompleteDialog(taskId: Int, position: Int) {
        val dialog = Dialog(context, R.style.AppTheme)
        dialog.setContentView(R.layout.dialog_completed_theme)
        dialog.findViewById<Button>(R.id.closeButton).setOnClickListener {
            deleteTaskFromId(taskId, position)
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun deleteTaskFromId(taskId: Int, position: Int) {
        class DeleteTaskAsync : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                DatabaseClient.getInstance(context)
                    ?.getAppDatabase()
                    ?.dataBaseAction()
                    ?.deleteTaskFromId(taskId)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                removeAtPosition(position)
                setRefreshListener.refresh()
            }
        }
        DeleteTaskAsync().execute()
    }

    private fun removeAtPosition(position: Int) {
        taskList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, taskList.size)
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                title.text = task.taskTitle
                description.text = task.taskDescrption
                time.text = task.lastAlarm
                status.text = if (task.isComplete) "COMPLETED" else "UPCOMING"
                options.setOnClickListener { view ->
                    showPopUpMenu(view, adapterPosition)
                }

                try {
                    val date = inputDateFormat.parse(task.date!!)
                    val outputDateString = dateFormat.format(date!!)
                    val items = outputDateString.split(" ")

                    day.text = items[0]
                    dateView.text = items[1]
                    month.text = items[2]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
