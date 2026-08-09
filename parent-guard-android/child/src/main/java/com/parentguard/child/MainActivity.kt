package com.parentguard.child

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parentguard.child.receiver.AdminReceiver
import com.parentguard.child.service.UsageMonitorService
import com.parentguard.child.ui.theme.ParentGuardTheme

/**
 * 孩子端主 Activity - 引导用户授权所有必要权限
 *
 * 首次启动：引导用户完成以下授权
 *   1. 使用情况访问权限
 *   2. 无障碍服务
 *   3. 设备管理员
 *   4. 位置权限
 *   5. 通知权限
 *
 * 授权完成后启动 UsageMonitorService，开始守护
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParentGuardTheme {
                SetupWizard(onAllDone = { onAllPermissionsGranted() })
            }
        }
    }

    private fun onAllPermissionsGranted() {
        // 启动前台服务
        UsageMonitorService.start(this)
        // 跳转到启动器
        startActivity(Intent(this, com.parentguard.child.launcher.LauncherActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWizard(onAllDone: () -> Unit) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) }
    val steps = listOf(
        Step("📊", "使用情况访问", "用于统计每个 APP 的使用时长") {
            context.startActivity(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        },
        Step("♿", "无障碍服务", "用于识别你打开了哪个应用，到达限时自动提醒") {
            context.startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        },
        Step("🛡️", "设备管理员", "防止本应用被卸载") {
            val adminComponent = ComponentName(context, AdminReceiver::class.java)
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "启用后防止家长助手被意外卸载")
            }
            context.startActivity(intent)
        },
        Step("📍", "位置权限", "用于实时位置和足迹") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        },
        Step("🔔", "通知权限", "用于显示守护通知") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 通知权限会通过 launcher 启动后再请求
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))
        Text("⏰", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text("欢迎使用家长助手", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("请完成以下授权以开启守护", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

        Spacer(Modifier.height(40.dp))

        // 步骤列表
        steps.forEachIndexed { i, s ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (i < step) Color(0xFFE8F8EC) else Color.White
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(s.emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(s.subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                    }
                    if (i < step) {
                        Text("✓", color = Color(0xFF34C759), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    } else if (i == step) {
                        TextButton(onClick = {
                            s.onClick()
                            step = i + 1
                        }) {
                            Text("去授权", color = Color(0xFF1A73E8))
                        }
                    } else {
                        Text("○", color = Color.LightGray, fontSize = 22.sp)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onAllDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
        ) {
            Text("完成，开始守护", fontSize = 16.sp)
        }

        Spacer(Modifier.height(24.dp))
    }
}

data class Step(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)
