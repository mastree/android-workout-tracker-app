package com.saraga.workoutapp.services


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.action!!.toInt()
        var msg = ""
        if (id % 2 == 0) {
            msg += "Waktunya berolahraga!"
        } else {
            msg += "Wah olahraga sudah selesai uwaw!"
        }
        val notificationHelper = NotificationHelper(context)
        val nb: NotificationCompat.Builder = notificationHelper.channelNotification
            .setContentTitle("Alarm!")
            .setContentText(msg)
        notificationHelper.manager!!.notify(id, nb.build())
    }
}