package com.parentguard.child.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.parentguard.child.ChildApp
import com.parentguard.child.R
import com.parentguard.child.data.DataStorePrefs
import com.parentguard.child.data.GuardEngine
import com.parentguard.child.data.SyncAgent
import kotlinx.coroutines.*

/**
 * 命令接收服务：持续轮询服务器,接收家长端下发的命令
 * 生产环境建议改为 WebSocket（已在 main.py 实现），这里用 HTTP 轮询做简化方案
 */
class CommandReceiver : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var running = false

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTI_ID, buildNoti())
        startPolling()
    }

    private fun startPolling() {
        if (running) return
        running = true
        scope.launch {
            while (isActive) {
                try {
                    SyncAgent.pollCommands(applicationContext) { response ->
                        if (response.contains("rules_updated")) {
                            GuardEngine.loadFromStorage(applicationContext)
                        }
                    }
                } catch (_: Exception) {}
                delay(15_000)
            }
        }
    }

    private fun buildNoti(): Notification {
        return NotificationCompat.Builder(this, ChildApp.CHANNEL_MONITOR)
            .setContentTitle("家长助手·命令接收中")
            .setContentText("正在同步家长设置")
            .setSmallIcon(R.drawable.ic_guard)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTI_ID = 1003

        fun start(ctx: Context) {
            val intent = Intent(ctx, CommandReceiver::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(intent)
            } else {
                ctx.startService(intent)
            }
        }
    }
}
