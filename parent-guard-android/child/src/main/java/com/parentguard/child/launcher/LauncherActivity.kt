package com.parentguard.child.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.parentguard.child.data.GuardEngine
import com.parentguard.child.ui.theme.ParentGuardTheme

/**
 * 启动器 - 成为默认桌面
 *
 * 关键点：
 * 1. AndroidManifest 中声明 android.intent.category.HOME → 用户可设为默认桌面
 * 2. 显示允许的应用 + 时长剩余
 * 3. 点击被禁用的应用 → 拉起 BlockedActivity
 */
class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParentGuardTheme {
                LauncherScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    val usage by GuardEngine.usage.collectAsState()
    val rules by GuardEngine.rules.collectAsState()
    val apps = remember { loadLauncherApps(context) }

    val totalUsedMin = usage.values.sum()
    val totalLimitMin = rules.totalLimitMinutes
    val remainingMin = (totalLimitMin - totalUsedMin).coerceAtLeast(0)
    val usagePercent = if (totalLimitMin > 0) totalUsedMin.toFloat() / totalLimitMin else 0f

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("下午好", fontSize = 14.sp, color = Color.Gray)
                    Text("二娃 👋", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    // 剩余时长卡
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⏰", fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "今天还能玩 $remainingMin 分钟",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        "已用 $totalUsedMin 分钟 / 共 $totalLimitMin 分钟",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = usagePercent.coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFFF9500),
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(padding).padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(apps) { app ->
                LauncherAppItem(app, rules) {
                    val action = GuardEngine.checkAccess(context, app.packageName)
                    if (action == GuardEngine.AccessAction.BLOCK) {
                        val intent = Intent().apply {
                            component = ComponentName(context, "com.parentguard.child.ui.BlockedActivity")
                            putExtra("package_name", app.packageName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } else {
                        val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
                        if (launch != null) context.startActivity(launch)
                    }
                }
            }
        }
    }
}

@Composable
fun LauncherAppItem(app: LauncherApp, rules: com.parentguard.child.data.RuleSet, onClick: () -> Unit) {
    val isBlocked = app.packageName in rules.appRules && rules.appRules[app.packageName]!!.blocked
    Column(
        modifier = Modifier.padding(8.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isBlocked) Color(0xFFCCCCCC) else Color(0xFF1A73E8)),
            contentAlignment = Alignment.Center
        ) {
            Text(app.icon, fontSize = 28.sp, color = Color.White)
            if (isBlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 22.sp)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            app.label,
            fontSize = 12.sp,
            color = if (isBlocked) Color.Gray else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

data class LauncherApp(
    val packageName: String,
    val label: String,
    val icon: String,
    val category: AppCategory
)

enum class AppCategory { STUDY, FUN, TOOL }

private fun loadLauncherApps(context: Context): List<LauncherApp> = listOf(
    LauncherApp("com.study.xiaoe", "小鹅通", "🎓", AppCategory.STUDY),
    LauncherApp("com.zuoyebang.help", "作业帮", "📖", AppCategory.STUDY),
    LauncherApp("com.pomodoro.app", "番茄钟", "📝", AppCategory.STUDY),
    LauncherApp("com.tencent.tmgp.pubgmhd", "和平精英", "🎮", AppCategory.FUN),
    LauncherApp("tv.danmaku.bili", "哔哩哔哩", "📺", AppCategory.FUN),
    LauncherApp("com.doubao", "豆包", "💬", AppCategory.FUN),
    LauncherApp("com.xingin.xhs", "小红书", "📕", AppCategory.FUN),
    LauncherApp("com.android.dialer", "电话", "📞", AppCategory.TOOL),
    LauncherApp("com.android.camera", "相机", "📷", AppCategory.TOOL),
    LauncherApp("com.android.settings", "设置", "⚙️", AppCategory.TOOL),
)
