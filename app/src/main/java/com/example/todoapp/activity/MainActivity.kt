package com.example.todoapp.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.todoapp.broadcastReceiver.AlarmBroadcastReceiver
import com.example.todoapp.bottomSheetFragment.CreateTaskBottomSheetFragment
import com.example.todoapp.database.DatabaseClient
import com.example.todoapp.R
import com.example.todoapp.bottomSheetFragment.ShowCalendarViewBottomSheet
import com.example.todoapp.model.Task
import com.example.todoapp.adapter.TaskAdapter
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), CreateTaskBottomSheetFragment.SetRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private var taskAdapter: TaskAdapter? = null
    private var tasks: MutableList<Task> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAdapter()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Enable AlarmBroadcastReceiver
        val receiver = ComponentName(this, AlarmBroadcastReceiver::class.java)
        val pm: PackageManager = packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // Load the image into the noDataImage
        Glide.with(applicationContext).load(R.drawable.first_note).into(binding.noDataImage)

        // Set up Add Task button listener
        binding.addTask.setOnClickListener {
            val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
            createTaskBottomSheetFragment.setTaskId(
                0,
                false,
                this,
                this@MainActivity
            )
            createTaskBottomSheetFragment.show(
                supportFragmentManager,
                createTaskBottomSheetFragment.tag
            )
        }

        // Load saved tasks
        loadSavedTasks()

        // Set up Calendar button listener
        binding.calendar.setOnClickListener {
            val showCalendarViewBottomSheet = ShowCalendarViewBottomSheet()
            showCalendarViewBottomSheet.show(
                supportFragmentManager,
                showCalendarViewBottomSheet.tag
            )
        }
    }

    private fun setUpAdapter() {
        taskAdapter = TaskAdapter(this, tasks, this)
        binding.taskRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.taskRecycler.adapter = taskAdapter
    }

    private fun loadSavedTasks() {
        class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                binding.noDataImage.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                setUpAdapter()
            }

            override fun doInBackground(vararg params: Void?): List<Task> {
                tasks = ((DatabaseClient.getInstance(applicationContext)
                    ?.getAppDatabase()
                    ?.dataBaseAction()
                    ?.getAllTasksList() ?: emptyList())).toMutableList()
                return tasks
            }
        }

        GetSavedTasks().execute()
    }

    override fun refresh() {
        loadSavedTasks()
    }
}
