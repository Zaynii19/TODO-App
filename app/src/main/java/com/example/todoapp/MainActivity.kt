package com.example.todoapp

import android.app.Dialog
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.utils.setupDialog

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val addTaskDialog : Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_task_dialog)
        }
    }
    private val updateTaskDialog : Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.update_task_dialog )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addCloseBtn = addTaskDialog.findViewById<ImageView>(R.id.closeBtn)
        val updateCloseBtn = updateTaskDialog.findViewById<ImageView>(R.id.closeBtn)

        addCloseBtn.setOnClickListener { addTaskDialog.dismiss() }
        updateCloseBtn.setOnClickListener { updateTaskDialog.dismiss() }

        binding.addTaskBtn.setOnClickListener {
            addTaskDialog.show()
        }

        val saveTaskBtn = addTaskDialog.findViewById<Button>(R.id.saveBtn)
        saveTaskBtn.setOnClickListener {

        }
    }

}