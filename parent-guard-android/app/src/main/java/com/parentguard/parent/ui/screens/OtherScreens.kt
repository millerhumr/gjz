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

/**
 * 占位实现：剩余的家长端页面（Time/Apps/AppDetail/AppBatch/Downtime/Content/Stats/Location/Approve/Pair）
 * 完整实现请参照 HomeScreen.kt 和 DashboardScreen.kt 的模式
 * 实际项目建议拆分到多个文件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "可用时长", "工作日 4h30m / 周末 2h / 暑假 6h")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "应用限额", "对单个或分组应用设置使用时长")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "应用设置", "设置时长/始终允许/禁止使用")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBatchScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "批量设置", "勾选多个应用统一设置")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DowntimeScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "停用时间", "23:00-08:00 每天 / 19:00-20:00 学习时段")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "内容限制", "网址黑白名单 / 内容分级 / 应用过滤")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "使用统计", "日均 3h12m · 周环比 -24m")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "位置与足迹", "福建省厦门市集美区")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproveScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "延时审批", "2 条待处理申请")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairScreen(navController: NavHostController) {
    PlaceholderScreen(navController, "添加设备", "二维码 + 6位配对码")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(navController: NavHostController, title: String, desc: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("‹", fontSize = 22.sp, color = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🚧", fontSize = 60.sp)
            Spacer(Modifier.height(16.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(desc, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Text(
                "完整 UI 实现请参照 HomeScreen.kt 和 DashboardScreen.kt",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
