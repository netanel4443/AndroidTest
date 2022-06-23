package com.e.androidtest.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.e.androidtest.R


class DistanceNotification {

    private var remote: RemoteViews? = null
    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null


    fun notificate(context: Context) {

        val channelId = context.packageName

        remote = RemoteViews(channelId, R.layout.gps_notification)

        builder = NotificationCompat.Builder(context, context.packageName).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setCustomContentView(remote)
            setSilent(true)

        }

        builder!!.setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }


        (context as Service).startForeground(1, builder!!.build())

    }


    fun updateNotification(context: Context, string: String) {
        remote!!.setTextViewText(R.id.notification_message, string)
        NotificationManagerCompat.from(context).notify(1, builder!!.build())
    }


}