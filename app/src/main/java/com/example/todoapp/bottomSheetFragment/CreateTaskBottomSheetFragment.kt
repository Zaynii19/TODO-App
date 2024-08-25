package com.example.todoapp.bottomSheetFragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.todoapp.broadcastReceiver.AlarmBroadcastReceiver
import com.example.todoapp.database.DatabaseClient
import com.example.todoapp.model.Task
import com.example.todoapp.activity.MainActivity
import com.example.todoapp.databinding.FragmentCreateTaskBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar
import java.util.GregorianCalendar

class CreateTaskBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private var taskId: Int = 0
    private var isEdit: Boolean = false
    private var task: Task? = null
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var setRefreshListener: SetRefreshListener? = null
    private var alarmManager: AlarmManager? = null
    private var timePickerDialog: TimePickerDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var activity: MainActivity? = null

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }


    fun setTaskId(
        taskId: Int,
        isEdit: Boolean,
        setRefreshListener: SetRefreshListener?,
        activity: MainActivity?
    ) {
        this.taskId = taskId
        this.isEdit = isEdit
        this.activity = activity
        this.setRefreshListener = setRefreshListener
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        _binding = FragmentCreateTaskBinding.inflate(layoutInflater)
        val contentView = binding.root
        dialog.setContentView(contentView)

        // Initialize the BottomSheetBehavior and set the callback
        val bottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        bottomSheetBehavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)

        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        binding.addTask.setOnClickListener {
            if (validateFields()) createTask()
        }

        if (isEdit) {
            showTaskFromId()
        }

        binding.taskDate.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]
                datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        binding.taskDate.setText(buildString {
                            append(dayOfMonth.toString())
                            append("-")
                            append((monthOfYear + 1))
                            append("-")
                            append(year)
                        })
                        datePickerDialog!!.dismiss()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog!!.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog!!.show()
            }
            true
        }

        binding.taskTime.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // Get Current Time
                val c = Calendar.getInstance()
                mHour = c[Calendar.HOUR_OF_DAY]
                mMinute = c[Calendar.MINUTE]

                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(
                    activity,
                    { _: TimePicker?, hourOfDay: Int, minute: Int ->
                        binding.taskTime.setText(buildString {
                            append(hourOfDay)
                            append(":")
                            append(minute)
                        })
                        timePickerDialog!!.dismiss()
                    }, mHour, mMinute, false
                )
                timePickerDialog!!.show()
            }
            true
        }
    }

    fun validateFields(): Boolean {
        return when {
            binding.addTaskTitle.text.toString().isBlank() -> {
                Toast.makeText(activity, "Please enter a valid title", Toast.LENGTH_SHORT).show()
                false
            }
            binding.addTaskDescription.text.toString().isBlank() -> {
                Toast.makeText(activity, "Please enter a valid description", Toast.LENGTH_SHORT).show()
                false
            }
            binding.taskDate.text.toString().isBlank() -> {
                Toast.makeText(activity, "Please enter date", Toast.LENGTH_SHORT).show()
                false
            }
            binding.taskTime.text.toString().isBlank() -> {
                Toast.makeText(activity, "Please enter time", Toast.LENGTH_SHORT).show()
                false
            }
            binding.taskEvent.text.toString().isBlank() -> {
                Toast.makeText(activity, "Please enter an event", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createTask() {
        class SaveTaskInBackend : AsyncTask<Void?, Void?, Void?>() {
            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: Void?): Void? {
                val createTask = Task().apply {
                    taskTitle = binding.addTaskTitle.text.toString()
                    taskDescrption = binding.addTaskDescription.text.toString()
                    date = binding.taskDate.text.toString()
                    lastAlarm = binding.taskTime.text.toString()
                    event = binding.taskEvent.text.toString()
                }

                if (!isEdit) {
                    DatabaseClient.getInstance(requireActivity())!!.getAppDatabase()
                        .dataBaseAction()
                        ?.insertDataIntoTaskList(createTask)
                } else {
                    DatabaseClient.getInstance(requireActivity())!!.getAppDatabase()
                        .dataBaseAction()
                        ?.updateAnExistingRow(
                            taskId,
                            binding.addTaskTitle.text.toString(),
                            binding.addTaskDescription.text.toString(),
                            binding.taskDate.text.toString(),
                            binding.taskTime.text.toString(),
                            binding.taskEvent.text.toString()
                        )
                }
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createAnAlarm()
                }
                setRefreshListener?.refresh()
                Toast.makeText(activity, "Your event has been added", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        SaveTaskInBackend().execute()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun createAnAlarm() {
        try {
            val items1 = binding.taskDate.text.toString().split("-").toTypedArray()
            val dd = items1[0]
            val month = items1[1]
            val year = items1[2]

            val itemTime = binding.taskTime.text.toString().split(":").toTypedArray()
            val hour = itemTime[0]
            val min = itemTime[1]

            val curCal: Calendar = GregorianCalendar()
            curCal.timeInMillis = System.currentTimeMillis()

            val cal: Calendar = GregorianCalendar().apply {
                set(Calendar.HOUR_OF_DAY, hour.toInt())
                set(Calendar.MINUTE, min.toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DATE, dd.toInt())
            }

            val alarmIntent = Intent(activity, AlarmBroadcastReceiver::class.java).apply {
                putExtra("TITLE", binding.addTaskTitle.text.toString())
                putExtra("DESC", binding.addTaskDescription.text.toString())
                putExtra("DATE", binding.taskDate.text.toString())
                putExtra("TIME", binding.taskTime.text.toString())
            }

            val pendingIntent = PendingIntent.getBroadcast(
                activity, count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager!!.apply {
                setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
            count++

            val intent = PendingIntent.getBroadcast(activity, count, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager!!.apply {
                setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis - 600000, intent)
                setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis - 600000, intent)
            }
            count++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showTaskFromId() {
        class ShowTaskFromId : AsyncTask<Void?, Void?, Void?>() {
            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: Void?): Void? {
                task = DatabaseClient.getInstance(requireActivity())!!.getAppDatabase()
                    .dataBaseAction()!!.selectDataFromAnId(taskId)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                task?.let {
                    binding.addTaskTitle.setText(it.taskTitle)
                    binding.addTaskDescription.setText(it.taskDescrption)
                    binding.taskDate.setText(it.date)
                    binding.taskTime.setText(it.lastAlarm)
                    binding.taskEvent.setText(it.event)
                }
            }
        }
        ShowTaskFromId().execute()
    }

    interface SetRefreshListener {
        fun refresh()
    }

    companion object {
        private var count = 0
    }
}
