package com.parentguard.child.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.parentguard.child.service.UsageMonitorService

/**
 * 开机自启动
 * 重启后自动恢复守护
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            UsageMonitorService.start(context)
        }
    }
}
