package com.parentguard.child.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.parentguard.child.data.model.AppRule
import com.parentguard.child.data.model.Downtime
import com.parentguard.child.data.model.TimeRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "guard_prefs")

/**
 * 规则本地持久化
 * - 启动时加载到 GuardEngine._rules
 * - 家长命令下发时更新
 */
object DataStorePrefs {
    private val KEY_RULES_JSON = stringPreferencesKey("rules_json")
    private val KEY_TOTAL_LIMIT = intPreferencesKey("total_limit_min")
    private val KEY_DOWNTIMES = stringPreferencesKey("downtimes_json")
    private val KEY_DEVICE_TOKEN = stringPreferencesKey("device_token")

    fun loadRules(ctx: Context): RuleSet = runBlocking {
        val prefs = ctx.dataStore.data.first()
        val totalLimit = prefs[KEY_TOTAL_LIMIT] ?: 270
        val token = prefs[KEY_DEVICE_TOKEN] ?: ""

        // 反序列化 JSON 规则
        val apps = parseAppRules(prefs[KEY_RULES_JSON])
        val downtimes = parseDowntimes(prefs[KEY_DOWNTIMES])

        RuleSet(
            appRules = apps.associateBy { it.packageName },
            totalLimitMinutes = totalLimit,
            downtimes = downtimes,
            alwaysAllowedPackages = setOf(
                "com.android.dialer", "com.android.contacts",
                "com.android.camera", "com.android.systemui"
            )
        )
    }

    fun saveRules(ctx: Context, rules: RuleSet) = runBlocking {
        ctx.dataStore.edit { prefs ->
            prefs[KEY_TOTAL_LIMIT] = rules.totalLimitMinutes
            prefs[KEY_RULES_JSON] = serializeAppRules(rules.appRules.values.toList())
            prefs[KEY_DOWNTIMES] = serializeDowntimes(rules.downtimes)
        }
    }

    fun saveDeviceToken(ctx: Context, token: String) = runBlocking {
        ctx.dataStore.edit { it[KEY_DEVICE_TOKEN] = token }
    }

    fun getDeviceToken(ctx: Context): String? = runBlocking {
        ctx.dataStore.data.first()[KEY_DEVICE_TOKEN]
    }

    // ---- 简化版 JSON 序列化（实际项目建议用 Moshi/Gson） ----
    private fun parseAppRules(json: String?): List<AppRule> {
        if (json.isNullOrEmpty()) return emptyList()
        // TODO: 实际解析
        return emptyList()
    }

    private fun parseDowntimes(json: String?): List<Downtime> {
        if (json.isNullOrEmpty()) return emptyList()
        return emptyList()
    }

    private fun serializeAppRules(rules: List<AppRule>): String {
        // TODO: 实际序列化
        return ""
    }

    private fun serializeDowntimes(list: List<Downtime>): String = ""
}
