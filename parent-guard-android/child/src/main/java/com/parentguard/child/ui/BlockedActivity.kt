package com.parentguard.child.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 * 受限提示页
 * 当孩子尝试打开被限/被禁的应用时被无障碍服务拉起
 */
class BlockedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParentGuardTheme {
                BlockedScreen(
                    packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: "",
                    onRequestTime = {
                        startActivity(Intent(this, RequestActivity::class.java))
                        finish()
                    },
                    onClose = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_CLASS_NAME = "class_name"
    }
}

@Composable
fun BlockedScreen(packageName: String, onRequestTime: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF0F7FF)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))
        Text("⏰", fontSize = 80.sp)
        Spacer(Modifier.height(20.dp))
        Text(
            "今天的时间用完啦",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "你已经使用了 4 小时 30 分钟\n明天早上 8:00 就可以继续使用啦",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        // 可继续使用列表
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8EC))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "✅ 还能继续使用的应用",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                AlwaysAllowedRow("⏰", "时钟")
                AlwaysAllowedRow("📷", "相机")
                AlwaysAllowedRow("🎓", "小鹅通学员版")
                AlwaysAllowedRow("📞", "电话")
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onRequestTime,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
        ) {
            Text("🙏 申请延时", fontSize = 15.sp)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("关闭", color = Color(0xFF1A73E8), fontSize = 14.sp)
        }
    }
}

@Composable
fun AlwaysAllowedRow(emoji: String, name: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(name, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("✓", color = Color(0xFF34C759), fontSize = 14.sp)
    }
}
