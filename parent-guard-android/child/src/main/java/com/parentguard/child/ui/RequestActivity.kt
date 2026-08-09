package com.parentguard.child.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parentguard.child.ui.theme.ParentGuardTheme

/**
 * 延时申请页
 * 孩子提交申请 → 家长端接收通知
 */
class RequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParentGuardTheme {
                RequestScreen(
                    onSubmit = { duration, reason ->
                        // TODO: 提交到服务器 / 推送给家长端
                        Toast.makeText(this, "已提交申请，等待家长审批", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(onSubmit: (Int, String) -> Unit, onCancel: () -> Unit) {
    var selectedDuration by remember { mutableStateOf(30) }
    var reason by remember { mutableStateOf("") }

    val durations = listOf(15, 30, 60, 90, 120, 180)
    val quickReasons = listOf("正在组队", "作业写完了", "周末想多玩会儿", "看完这集就停", "考试考好了")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("申请延时") },
                navigationIcon = {
                    TextButton(onClick = onCancel) { Text("取消") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onSubmit(selectedDuration, reason) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Text("📨 提交申请", fontSize = 15.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头部
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFE8F0FE)),
                contentAlignment = Alignment.Center
            ) {
                Text("🙏", fontSize = 32.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text("请家长延长你的使用时间", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("提交后会立即通知爸爸妈妈", fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(24.dp))

            // 时长选择
            Text("⏱️ 申请时长", fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                items(durations) { d ->
                    val label = if (d >= 60) "${d / 60} 小时" else "$d 分钟"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedDuration == d) Color(0xFFE8F0FE) else Color.White)
                            .clickable { selectedDuration = d }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (selectedDuration == d) Color(0xFF1A73E8) else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 理由输入
            Text("💬 申请理由（选填）", fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                placeholder = { Text("告诉爸爸妈妈为什么需要延时...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(Modifier.height(8.dp))
            // 快捷标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickReasons.take(3).forEach { r ->
                    SuggestionChip(
                        onClick = { reason += r },
                        label = { Text(r, fontSize = 12.sp) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickReasons.drop(3).forEach { r ->
                    SuggestionChip(
                        onClick = { reason += r },
                        label = { Text(r, fontSize = 12.sp) }
                    )
                }
            }
        }
    }
}
