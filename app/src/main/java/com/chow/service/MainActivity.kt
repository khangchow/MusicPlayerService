package com.chow.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.chow.service.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val song = Song(
        "Simple Music Player Service",
        "Chow Minh Khang",
        R.drawable.ic_song,
        R.raw.song
    )
    private val sharedPreferences by lazy {
        this.getSharedPreferences(
            this.packageName,
            Context.MODE_PRIVATE
        )
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            if (sharedPreferences.getBoolean(MusicPlayer.KEY_IS_SHOWING_CONTROLLER, false)) {
                showMusicController(
                    Gson().fromJson(
                        sharedPreferences.getString(MusicPlayer.KEY_SONG_INFO, Song().toString()),
                        Song::class.java
                    )
                )
            }
            EventBus.getLiveData().observe(this@MainActivity) {
                if (EventBus.hasNewEvent) {
                    bsSong.apply {
                        when (it.key) {
                            MusicPlayer.KEY_PLAY -> {
                                showMusicController()
                            }
                            MusicPlayer.KEY_PAUSE -> {
                                showMusicController()
                            }
                            MusicPlayer.KEY_RESUME -> {
                                showMusicController()
                            }
                            MusicPlayer.KEY_STOP_SERVICE -> {
                                rlParent.invisible()
                            }
                        }
                    }
                }
            }
            bsSong.ivClear.setOnClickListener { stopPlayingMusicService() }
            btnStart.setOnClickListener {
                startPlayingMusicService()
            }
        }
    }

    private fun showMusicController(song: Song? = null) {
        val currentSong = song ?: MusicPlayer.song
        binding.bsSong.apply {
            currentSong?.let {
                rlParent.visible()
                tvTitle.text = it.title
                tvSinger.text = it.singer
                ivSong.setImageResource(R.drawable.ic_song)
                ivPlayOrPause.apply {
                    if (MusicPlayer.isPlaying) {
                        setImageResource(R.drawable.ic_pause)
                        setOnClickListener {
                            onUpdateMusicService(MyService.ACTION_PAUSE)
                        }
                    } else {
                        setImageResource(R.drawable.ic_play)
                        setOnClickListener {
                            onUpdateMusicService(MyService.ACTION_RESUME)
                        }
                    }
                }
            }
        }
    }

    private fun onUpdateMusicService(action: Int) {
        Intent(this, MyService::class.java).apply {
            putExtra(MyService.KEY_ACTION, action)
            startService(this)
        }
    }

    private fun startPlayingMusicService() {
        startService(
            Intent(this, MyService::class.java).apply {
                putExtras(
                    bundleOf(
                        MyService.KEY_SONG to song
                    )
                )
            }
        )
    }

    private fun stopPlayingMusicService() {
        stopService(Intent(this, MyService::class.java))
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    if (Build.VERSION.SDK_INT >= 33) {
                        requestPermissionLauncher.launch(
                            android.Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            }
        }
    }
}