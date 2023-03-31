package com.chow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(
            Intent(context, MyService::class.java).apply {
                putExtra(MyService.KEY_ACTION, intent.getIntExtra(MyService.KEY_ACTION, 0))
            }
        )
    }
}