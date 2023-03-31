package com.chow.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

object NotificationUtils {
     fun createSongControllerNotification(context: Context, remoteViews: RemoteViews) =
         NotificationCompat.Builder(context, MyService.CHANNEL_ID)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
             .setSmallIcon(R.drawable.ic_song)
             .setContentIntent(
                 PendingIntent.getActivity(
                     context,
                     0,
                     Intent(context, MainActivity::class.java),
                     getPendingIntentFlag()
                 )
             )
             .setCustomContentView(
                 remoteViews
             )
             .setSound(null)
             .setAutoCancel(false)
             .setOngoing(true)
             .setOnlyAlertOnce(true)
             .build()

    fun getPendingIntentFlag() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
}