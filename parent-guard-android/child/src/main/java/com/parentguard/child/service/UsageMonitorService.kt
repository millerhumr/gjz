package com.parentguard.child.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.parentguard.child.ChildApp
import com.parentguard.child.R
import com.parentguard.child.data.GuardEngine
import kotlinx.coroutines.*
import java.util.Calendar

/**
 * 应用使用统计服务
 *
 * 职责：
 * 1. 每 30 秒轮询一次系统 UsageStats
 * 2. 累加各应用今日使用时长
 * 3. 命中规则时触发拦截（与无障碍服务互为备份）
 * 4. 同步数据给家长端
 */
class UsageMonitorService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var usageStatsManager: UsageStatsManager

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        startForeground(NOTIFICATION_ID, buildNotification())
        startMonitoring()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                try {
                    tickUsage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(30_000) // 30秒轮询
            }
        }
    }

    private suspend fun tickUsage() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis

        val events = usageStatsManager.queryEvents(startOfDay, now)
        val event = UsageEvents.Event()
        val statsMap = mutableMapOf<String, Long>()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val pkg = event.packageName ?: continue
                val lastResume = statsMap[pkg] ?: 0L
                if (event.timeStamp > lastResume) {
                    statsMap[pkg] = event.timeStamp
                }
            }
        }

        // 转换为分钟数
        val usageMinutes = statsMap.mapValues { (_, lastResume) ->
            ((now - lastResume) / 60_000).toInt().coerceAtLeast(0)
        }

        // 喂给 GuardEngine 决策
        GuardEngine.onUsageTick(this@UsageMonitorService, usageMinutes)

        // 上报家长端
        com.parentguard.child.data.SyncAgent.uploadUsage(this@UsageMonitorService, usageMinutes)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, ChildApp.CHANNEL_MONITOR)
            .setContentTitle("家长助手·守护中")
            .setContentText("正在保护孩子的健康使用")
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
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, UsageMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, UsageMonitorService::class.java))
        }
    }
}
