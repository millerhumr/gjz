package com.parentguard.parent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.parentguard.parent.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的设备", fontWeight = FontWeight.SemiBold) },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.Pair) }) {
                        Text("+ 添加", color = Color(0xFF1A73E8))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("早上好，爸爸", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("已守护 2 台设备", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
            }

            // 设备1：在线
            item {
                DeviceCard(
                    name = "二娃",
                    avatar = "👧",
                    device = "优畅享20 5G",
                    online = true,
                    usedMin = 212,
                    limitMin = 270,
                    location = "厦门·集美",
                    onClick = { navController.navigate(Routes.Dashboard) }
                )
            }

            // 设备2：离线
            item {
                DeviceCard(
                    name = "大娃",
                    avatar = "👦",
                    device = "MatePad 11",
                    online = false,
                    usedMin = 145,
                    limitMin = 360,
                    location = "厦门·思明",
                    onClick = { navController.navigate(Routes.Dashboard) }
                )
            }

            // 快捷操作
            item {
                Spacer(Modifier.height(8.dp))
                Text("快捷操作", fontSize = 13.sp, color = Color.Gray)
            }
            item {
                QuickActionRow("🔔", "延时申请", "2 条待处理", badge = "2", badgeColor = Color(0xFFFF3B30)) {
                    navController.navigate(Routes.Approve)
                }
            }
            item {
                QuickActionRow("📍", "位置与足迹", "查看孩子当前位置与历史轨迹") {
                    navController.navigate(Routes.Location)
                }
            }
            item {
                QuickActionRow("📊", "使用报告", "近 7 天详细使用统计") {
                    navController.navigate(Routes.Stats)
                }
            }
        }
    }
}

@Composable
fun DeviceCard(
    name: String,
    avatar: String,
    device: String,
    online: Boolean,
    usedMin: Int,
    limitMin: Int,
    location: String,
    onClick: () -> Unit
) {
    val percent = if (limitMin > 0) usedMin.toFloat() / limitMin else 0f
    val usedStr = "${usedMin / 60}h${usedMin % 60}m"
    val limitStr = "${limitMin / 60}h${limitMin % 60}m"
    val remaining = (limitMin - usedMin).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB6D9)),
                    contentAlignment = Alignment.Center
                ) { Text(avatar, fontSize = 22.sp) }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (online) Color(0xFF34C759) else Color.Gray)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("$device · ${if (online) "守护中" else "离线"}",
                            fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Text("›", fontSize = 20.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("今日已用", fontSize = 12.sp, color = Color.Gray)
                Text("$usedStr / $limitStr", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = percent.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = if (percent > 0.75f) Color(0xFFFF9500) else Color(0xFF1A73E8),
                trackColor = Color(0xFFEEEEEE)
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF4E5)
                ) {
                    Text("剩余 $remaining 分钟",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp, color = Color(0xFFFF9500))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F0FE)
                ) {
                    Text("📍 $location",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp, color = Color(0xFF1A73E8))
                }
            }
        }
    }
}

@Composable
fun QuickActionRow(
    emoji: String,
    title: String,
    sub: String,
    badge: String? = null,
    badgeColor: Color = Color(0xFFFF3B30),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(badge, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
            }
            Text("›", fontSize = 18.sp, color = Color.Gray)
        }
    }
}
