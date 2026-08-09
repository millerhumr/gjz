package com.parentguard.parent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.parentguard.parent.ui.Routes

/**
 * 设备详情主页 - 核心页面
 * 对标华为家长助手截图 4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("二娃", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("‹", fontSize = 22.sp, color = Color.Black)
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
            // 设备信息卡
            item { DeviceInfoCard() }

            // 4大功能入口
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FeatureEntry("⏳", "可用时长", Color(0xFF34C759)) { navController.navigate(Routes.Time) }
                        FeatureEntry("📱", "应用限额", Color(0xFF5AC8FA)) { navController.navigate(Routes.Apps) }
                        FeatureEntry("🌙", "停用时间", Color(0xFF1A73E8)) { navController.navigate(Routes.Downtime) }
                        FeatureEntry("🔞", "内容限制", Color(0xFFFF3B30)) { navController.navigate(Routes.Content) }
                    }
                }
            }

            // 使用统计
            item { UsageStatCard(onClick = { navController.navigate(Routes.Stats) }) }

            // 应用使用列表
            item { AppUsageCard(onClick = { navController.navigate(Routes.Apps) }) }

            // 位置
            item { LocationCard(onClick = { navController.navigate(Routes.Location) }) }
        }
    }
}

@Composable
fun DeviceInfoCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFB6D9)),
                contentAlignment = Alignment.Center
            ) { Text("👧", fontSize = 28.sp) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("二娃", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34C759))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("优畅享20 5G · 守护中", fontSize = 13.sp, color = Color.Gray)
                }
                Text("更新于 2026/07/28 13:51", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun FeatureEntry(emoji: String, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 22.sp) }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun UsageStatCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📊", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text("使用统计", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("详细 ›", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("3", fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                        Text(" 小时 ", fontSize = 14.sp, color = Color.Gray)
                        Text("32", fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                        Text(" 分钟", fontSize = 14.sp, color = Color.Gray)
                    }
                    Text("今天屏幕使用时间", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("限时", fontSize = 12.sp, color = Color.Gray)
                    Text("4 小时 30 分钟", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A73E8))
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = 0.79f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFFFF9500),
                trackColor = Color(0xFFEEEEEE)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("没有统计", fontSize = 12.sp, color = Color.Gray)
                Text("后台已使用 24 分钟", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AppUsageCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📈", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text("应用使用", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onClick) { Text("管理 ›", color = Color(0xFF1A73E8)) }
            }
            Spacer(Modifier.height(8.dp))
            AppUsageRow("🎮", "和平精英", 119, 120, over = true, onClick = onClick)
            Divider(color = Color(0xFFEEEEEE))
            AppUsageRow("💬", "豆包", 63, 60, over = false, onClick = onClick)
            Divider(color = Color(0xFFEEEEEE))
            AppUsageRow("📺", "哔哩哔哩", 28, 180, over = false, onClick = onClick)
        }
    }
}

@Composable
fun AppUsageRow(emoji: String, name: String, used: Int, limit: Int, over: Boolean, onClick: () -> Unit) {
    val percent = if (limit > 0) used.toFloat() / limit else 0f
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 20.sp) }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    "${used / 60}h${used % 60}m",
                    fontSize = 13.sp,
                    color = if (over) Color(0xFFFF3B30) else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = percent.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = if (over) Color(0xFFFF3B30) else Color(0xFF1A73E8),
                trackColor = Color(0xFFEEEEEE)
            )
        }
    }
}

@Composable
fun LocationCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("📍", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("当前位置", fontWeight = FontWeight.SemiBold)
                    Text("福建省厦门市集美区", fontSize = 13.sp)
                    Text("更新于 2026/07/28 13:51", fontSize = 12.sp, color = Color.Gray)
                }
            }
            // 模拟地图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Color(0xFFB5DBE8)
                    )
            ) {
                Text("🗺️", modifier = Modifier.align(Alignment.Center), fontSize = 30.sp)
            }
        }
    }
}
