package com.parentguard.child.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.parentguard.child.data.GuardEngine
import com.parentguard.child.ui.BlockedActivity

/**
 * 家长控制无障碍服务
 *
 * 核心功能：
 * 1. 监听窗口变化，识别孩子打开了哪个应用
 * 2. 命中拦截规则时，把目标 Activity 拉回到 BlockedActivity
 * 3. 与 UsageMonitorService 配合实现"时间到/被禁用"的双重保险
 *
 * 为什么需要无障碍服务：
 * - Android 没有提供"应用启动拦截"的标准 API
 * - Android 10+ 限制后台 Activity 启动
 * - 设备管理员 + 无障碍 是当前最可靠的方案
 */
class GuardAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "GuardA11y"
        var instance: GuardAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "无障碍服务已连接")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        Log.w(TAG, "无障碍服务被解绑")
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        // 跳过自己
        if (pkg == packageName) return
        // 跳过系统
        if (isSystemApp(pkg)) return

        // 检查是否应该拦截
        val action = GuardEngine.checkAccess(this, pkg)
        if (action == GuardEngine.AccessAction.BLOCK) {
            Log.i(TAG, "拦截应用启动: $pkg")
            launchBlockedActivity(pkg, event.className?.toString())
        }
    }

    private fun launchBlockedActivity(pkg: String, cls: String?) {
        val intent = Intent(this, BlockedActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(BlockedActivity.EXTRA_PACKAGE_NAME, pkg)
            putExtra(BlockedActivity.EXTRA_CLASS_NAME, cls)
        }
        // Android 10+ 后台启动限制处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "无法启动拦截页: ${e.message}")
            }
        } else {
            startActivity(intent)
        }
        // 立即把目标 app 推到后台
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    private fun isSystemApp(pkg: String): Boolean {
        return pkg.startsWith("com.android.systemui") ||
               pkg.startsWith("com.android.settings") ||
               pkg.startsWith("android") ||
               pkg == "com.android.launcher3" ||
               pkg == "com.google.android.launcher" ||
               pkg == "com.huawei.android.launcher"
    }

    override fun onInterrupt() {
        Log.w(TAG, "无障碍服务被中断")
    }
}
