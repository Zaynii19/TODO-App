package com.example.todoapp.bottomSheetFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.applandeo.materialcalendarview.EventDay
import com.example.todoapp.database.DatabaseClient
import com.example.todoapp.R
import com.example.todoapp.model.Task
import com.example.todoapp.databinding.FragmentCalendarViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class ShowCalendarViewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentCalendarViewBinding? = null
    private val binding get() = _binding!!
    private var tasks: List<Task> = ArrayList()

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding if needed
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        _binding = FragmentCalendarViewBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        // Set up BottomSheetBehavior and attach callback
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)

        // Load saved tasks
        loadSavedTasks()

        // Set up back button listener
        binding.back.setOnClickListener { dialog.dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSavedTasks() {
        class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
            override fun doInBackground(vararg params: Void?): List<Task> {
                tasks = (DatabaseClient.getInstance(requireActivity())
                    ?.getAppDatabase()
                    ?.dataBaseAction()
                    ?.getAllTasksList() ?: emptyList()) as List<Task>
                return tasks
            }

            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                binding.calendarView.setEvents(highLightedDays)
            }
        }
        GetSavedTasks().execute()
    }

    private val highLightedDays: List<EventDay>
        get() {
            val events: MutableList<EventDay> = ArrayList()
            for (task in tasks) {
                val calendar = Calendar.getInstance()
                val items = task.date!!.split("-")
                val day = items[0].toInt()
                val month = items[1].toInt() - 1
                val year = items[2].toInt()

                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.YEAR, year)
                events.add(EventDay(calendar, R.drawable.dot))
            }
            return events
        }
}
