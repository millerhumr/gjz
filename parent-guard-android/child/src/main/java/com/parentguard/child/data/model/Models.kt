package com.parentguard.child.data.model

/** 应用使用规则 */
data class AppRule(
    val packageName: String,
    val appName: String,
    val ruleType: String = "time_limit",  // time_limit / always_allow / blocked
    val timeLimitMinutes: Int = 60,
    val alwaysAllowed: Boolean = false,
    val blocked: Boolean = false,
    val groupName: String? = null,
)

/** 总时长规则 */
data class TimeRule(
    val id: Int = 0,
    val name: String,
    val days: String = "1,2,3,4,5",  // 周一至周五
    val totalMinutes: Int = 270,  // 4h30m
    val dateRangeStart: String? = null,
    val dateRangeEnd: String? = null,
)

/** 停用时段 */
data class Downtime(
    val id: Int = 0,
    val name: String,
    val startTime: String,  // "23:00"
    val endTime: String,    // "08:00"
    val days: String = "1,2,3,4,5,6,7",
    val grayScreen: Boolean = false,
    val blockApps: Boolean = true,
)

/** 延时申请 */
data class TimeRequest(
    val id: Int = 0,
    val type: String = "app_time",  // app_time / total_time
    val targetPackage: String? = null,
    val targetAppName: String? = null,
    val minutes: Int = 30,
    val reason: String? = null,
    val status: String = "pending",  // pending / approved / rejected
    val createdAt: Long = 0L,
    val childName: String? = null,
)
