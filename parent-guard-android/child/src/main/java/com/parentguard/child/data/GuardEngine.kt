package com.parentguard.child.data

import android.content.Context
import com.parentguard.child.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 守护引擎 - 单一决策中心
 *
 * 接收家长下发的规则 + 实时使用数据 → 决定是否拦截
 *
 * 持久化使用 DataStore（详见 DataStorePrefs）
 * 规则同步使用 SyncAgent
 */
object GuardEngine {

    private val _rules = MutableStateFlow(RuleSet())
    val rules: StateFlow<RuleSet> = _rules

    private val _usage = MutableStateFlow<Map<String, Int>>(emptyMap())
    val usage: StateFlow<Map<String, Int>> = _usage

    enum class AccessAction { ALLOW, BLOCK }

    fun loadFromStorage(ctx: Context) {
        // 从 DataStore 加载规则
        val rules = DataStorePrefs.loadRules(ctx)
        _rules.value = rules
    }

    fun onUsageTick(ctx: Context, usageMap: Map<String, Int>) {
        _usage.value = usageMap
    }

    fun checkAccess(ctx: Context, packageName: String): AccessAction {
        val rule = _rules.value.appRules[packageName] ?: return AccessAction.ALLOW
        if (rule.blocked) return AccessAction.BLOCK
        if (rule.alwaysAllowed) return AccessAction.ALLOW

        // 检查总时长
        val current = _usage.value[packageName] ?: 0
        if (current >= rule.timeLimitMinutes) return AccessAction.BLOCK

        // 检查今日总时长
        val totalUsed = _usage.value.values.sum()
        if (totalUsed >= _rules.value.totalLimitMinutes) return AccessAction.BLOCK

        return AccessAction.ALLOW
    }

    fun isInDowntime(now: Long): Boolean {
        // 简化版：从 _rules.value.downtimes 判断
        return false
    }
}

/** 规则集合 */
data class RuleSet(
    val appRules: Map<String, AppRule> = emptyMap(),
    val totalLimitMinutes: Int = 270,  // 4小时30分
    val downtimes: List<Downtime> = emptyList(),
    val alwaysAllowedPackages: Set<String> = setOf(
        "com.android.dialer", "com.android.contacts",
        "com.android.camera", "com.android.systemui"
    )
)
