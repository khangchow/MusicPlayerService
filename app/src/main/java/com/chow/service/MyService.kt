package com.chow.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.chow.service.MusicPlayer.pause
import com.chow.service.MusicPlayer.resume
import com.chow.service.NotificationUtils.getPendingIntentFlag
import java.io.Serializable

class MyService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            it.getSerializable(KEY_SONG, Song::class.java)?.let { song ->
                MusicPlayer.setNotificationAction { sendNotification(song) }
                    .setSong(song)
                    .play()
                sendNotification(song)
            }
            handleActionMusic(it.getIntExtra(KEY_ACTION, 0))
        }
        return START_NOT_STICKY
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PAUSE -> pause()
            ACTION_RESUME -> resume()
            ACTION_CLEAR -> stopSelf()
        }
    }

    private fun sendNotification(song: Song) {
        startForeground(
            1,
            NotificationUtils.createSongControllerNotification(
                this,
                RemoteViews(packageName, R.layout.layout_custom_noti).apply {
                    setTextViewText(R.id.tv_title, song.title)
                    setTextViewText(R.id.tv_singer, song.singer)
                    setImageViewResource(R.id.iv_song, song.image)
                    if (MusicPlayer.isPlaying) {
                        setOnClickPendingIntent(
                            R.id.iv_play_or_pause,
                            getSongControllerPendingIntent(ACTION_PAUSE)
                        )
                        setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_pause)
                    } else {
                        setOnClickPendingIntent(
                            R.id.iv_play_or_pause,
                            getSongControllerPendingIntent(ACTION_RESUME)
                        )
                        setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_play)
                    }
                    setOnClickPendingIntent(R.id.iv_clear, getSongControllerPendingIntent(ACTION_CLEAR))
                }
            )
        )
    }

    private fun getSongControllerPendingIntent(action: Int) = PendingIntent.getBroadcast(
        this.applicationContext,
        action,
        Intent(this, MyReceiver::class.java).apply {
            putExtra(KEY_ACTION, action)
        },
        getPendingIntentFlag()
    )

    override fun onDestroy() {
        MusicPlayer.onServiceDestroyed()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val CHANNEL_NAME = "CHANNEL_NAME"
        const val KEY_SONG = "KEY_SONG"
        const val KEY_ACTION = "KEY_ACTION"
        const val ACTION_PAUSE = 1
        const val ACTION_RESUME = 2
        const val ACTION_CLEAR = 3
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable?> Intent.getSerializable(key: String?, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getSerializableExtra(key, clazz)
    else
        getSerializableExtra(key) as T
}