package com.example.todoapp.activity

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.notification)
        mediaPlayer!!.start()

        intent.extras?.let {
            binding.title.text = it.getString("TITLE")
            binding.description.text = it.getString("DESC")
            binding.timeAndData.text = buildString {
                append(it.getString("DATE"))
                append(", ")
                append(it.getString("TIME"))
            }
        }

        Glide.with(applicationContext).load(R.drawable.alert).into(binding.imageView)
        binding.closeButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
