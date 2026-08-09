package com.parentguard.child

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ChildApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        com.parentguard.child.data.GuardEngine.loadFromStorage(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(
                CHANNEL_MONITOR, "守护服务", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "前台服务通知，显示守护状态" })
            nm.createNotificationChannel(NotificationChannel(
                CHANNEL_ALERT, "时长提醒", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "时长到了或应用被限制时提醒" })
        }
    }

    companion object {
        const val CHANNEL_MONITOR = "monitor"
        const val CHANNEL_ALERT = "alert"
    }
}
