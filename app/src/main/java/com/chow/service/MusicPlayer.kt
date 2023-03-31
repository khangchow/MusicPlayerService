package com.chow.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson

@SuppressLint("StaticFieldLeak")
object MusicPlayer {
    private const val KEY_IS_PLAYING = "KEY_IS_PLAYING"
    const val KEY_SONG_INFO = "KEY_SONG_INFO"
    const val KEY_IS_SHOWING_CONTROLLER = "KEY_IS_SHOWING_CONTROLLER"
    const val KEY_PLAY = "KEY_PLAY"
    const val KEY_PAUSE = "KEY_PAUSE"
    const val KEY_RESUME = "KEY_RESUME"
    const val KEY_STOP_SERVICE = "KEY_STOP_SERVICE"
    private var mediaPlayer: MediaPlayer? = null
    @SuppressLint("StaticFieldLeak")
    private var context: Context? = null
    var isPlaying = false
    var song: Song? = null
    private var showNotificationAction: (() -> Unit)? = null
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) = this.also {
        this.context = context
        this.sharedPreferences = context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }

    fun setNotificationAction(action: () -> Unit) = this.also {
        this.showNotificationAction = action
    }

    fun setSong(song: Song) = this.also {
        context?.let {
            clearData()
            mediaPlayer = MediaPlayer.create(it, song.resource)
            this.song = song
            sharedPreferences.edit().putString(KEY_SONG_INFO, Gson().toJson(song)).apply()
        }
    }

    fun play() = this.also {
        if (mediaPlayer != null && showNotificationAction != null) {
            mediaPlayer!!.start()
            mediaPlayer!!.isLooping = true
            isPlaying = isPlaying.not()
            showNotificationAction!!.invoke()
            EventBus.emitEvent(Event(KEY_PLAY))
            sharedPreferences.edit().putBoolean(KEY_IS_PLAYING, true).apply()
            sharedPreferences.edit().putBoolean(KEY_IS_SHOWING_CONTROLLER, true).apply()
        }
    }

    fun pause() = this.also {
        if (mediaPlayer != null && isPlaying && showNotificationAction != null) {
            mediaPlayer!!.pause()
            isPlaying = isPlaying.not()
            showNotificationAction!!.invoke()
            EventBus.emitEvent(Event(KEY_PAUSE))
            sharedPreferences.edit().putBoolean(KEY_IS_PLAYING, false).apply()
        }
    }

    fun resume() = this.also {
        if (mediaPlayer != null && isPlaying.not() && showNotificationAction != null) {
            mediaPlayer!!.start()
            isPlaying = isPlaying.not()
            showNotificationAction!!.invoke()
            EventBus.emitEvent(Event(KEY_RESUME))
            sharedPreferences.edit().putBoolean(KEY_IS_PLAYING, true).apply()
        }
    }

    fun onServiceDestroyed() = this.also {
        showNotificationAction = null
        clearData()
        EventBus.emitEvent(Event(KEY_STOP_SERVICE))
        sharedPreferences.edit().putBoolean(KEY_IS_PLAYING, false).apply()
        sharedPreferences.edit().putBoolean(KEY_IS_SHOWING_CONTROLLER, false).apply()
    }

    private fun clearData() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
            isPlaying = false
            song = null
        }
    }
}