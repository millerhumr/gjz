package com.parentguard.child.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * 设备管理员接收器
 *
 * 防卸载核心：用户必须先在家长端关闭设备管理权限才能卸载本应用
 */
class AdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "家长助手·设备管理已启用", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        // 家长端关闭设备管理时，可以加密码二次确认
        return "关闭后家长助手将无法防止被卸载，确定要继续吗？"
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "家长助手·设备管理已关闭", Toast.LENGTH_SHORT).show()
    }
}
