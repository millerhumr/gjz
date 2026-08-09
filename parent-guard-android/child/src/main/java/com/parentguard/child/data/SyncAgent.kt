package com.parentguard.child.data

import android.content.Context
import android.util.Log
import com.parentguard.child.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 与家长端通信的轻量 Agent
 *
 * 通信方式选择：
 * - 简单方案：HTTP 轮询（每 5 分钟上报一次）
 * - 推荐方案：WebSocket / MQTT 长连接
 *
 * 本实现用 HTTP 轮询，生产环境建议替换为 MQTT
 */
object SyncAgent {
    private const val TAG = "SyncAgent"
    private const val SERVER_BASE = BuildConfig.SYNC_BASE_URL  // 在 build.gradle 配置

    private val client = OkHttpClient.Builder().build()

    fun uploadUsage(ctx: Context, usage: Map<String, Int>) {
        val token = DataStorePrefs.getDeviceToken(ctx) ?: return
        val payload = buildString {
            append("{\"token\":\"$token\",\"usage\":[")
            usage.entries.forEachIndexed { i, (pkg, min) ->
                if (i > 0) append(",")
                append("{\"pkg\":\"$pkg\",\"min\":$min}")
            }
            append("]}")
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val req = Request.Builder()
                    .url("$SERVER_BASE/api/usage")
                    .post(payload.toRequestBody("application/json".toMediaType()))
                    .build()
                client.newCall(req).execute().close()
            } catch (e: Exception) {
                Log.w(TAG, "上传用量失败: ${e.message}")
            }
        }
    }

    fun pollCommands(ctx: Context, onCommand: (String) -> Unit) {
        val token = DataStorePrefs.getDeviceToken(ctx) ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val req = Request.Builder()
                    .url("$SERVER_BASE/api/commands?token=$token")
                    .get()
                    .build()
                val resp = client.newCall(req).execute()
                val body = resp.body?.string() ?: return@launch
                onCommand(body)
            } catch (e: Exception) {
                Log.w(TAG, "拉取命令失败: ${e.message}")
            }
        }
    }
}
