package com.example.todoapp.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.activity.AlarmActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private var title: String? = null
    private var desc: String? = null
    private var date: String? = null
    private var time: String? = null
    override fun onReceive(context: Context, intent: Intent) {
        title = intent.getStringExtra("TITLE")
        desc = intent.getStringExtra("DESC")
        date = intent.getStringExtra("DATE")
        time = intent.getStringExtra("TIME")

        val i = Intent(context, AlarmActivity::class.java)
        i.putExtra("TITLE", title)
        i.putExtra("DESC", desc)
        i.putExtra("DATE", date)
        i.putExtra("TIME", time)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}