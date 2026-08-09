package com.parentguard.parent.data.model

import java.util.UUID

/** 孩子设备 */
data class ChildDevice(
    val id: String = UUID.randomUUID().toString(),
    val name: String,           // "二娃"
    val avatar: String,         // emoji
    val deviceModel: String,    // "优畅享20 5G"
    val online: Boolean,
    val lastSync: Long,         // timestamp
    val location: String? = null,
    val battery: Int? = null,
)

/** 应用限额规则 */
data class AppRule(
    val packageName: String,
    val appName: String,
    val icon: String? = null,
    val type: RuleType = RuleType.TIME_LIMIT,
    val timeLimitMinutes: Int = 60,    // 每天时长
    val allowBackground: Boolean = false,  // 是否计入后台
    val alwaysAllowed: Boolean = false,
    val blocked: Boolean = false,
    val groupName: String? = null,    // 分组名
)

enum class RuleType { TIME_LIMIT, ALWAYS_ALLOW, BLOCKED }

/** 可用时长 */
data class TimeRule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,         // "工作日" "周末" "暑假"
    val daysOfWeek: Set<Int>, // 1-7 (周一-周日)
    val dateRange: Pair<Long, Long>? = null,  // 自定义日期范围
    val totalMinutes: Int,    // 每天可用分钟
)

/** 停用时段 */
data class Downtime(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startTime: Pair<Int, Int>,   // (hour, minute)
    val endTime: Pair<Int, Int>,
    val daysOfWeek: Set<Int>,
    val grayScreen: Boolean = false,
    val blockApps: Boolean = true,
)

/** 延时申请 */
data class TimeRequest(
    val id: String,
    val deviceId: String,
    val childName: String,
    val type: RequestType,
    val targetPackage: String?,
    val targetAppName: String?,
    val minutes: Int,
    val reason: String?,
    val createdAt: Long,
    val status: RequestStatus = RequestStatus.PENDING,
)

enum class RequestType { APP_TIME, TOTAL_TIME }
enum class RequestStatus { PENDING, APPROVED, REJECTED }

/** 今日使用统计 */
data class UsageStat(
    val date: Long,
    val totalUsedMinutes: Int,
    val totalLimitMinutes: Int,
    val apps: List<AppUsage>,
)

data class AppUsage(
    val packageName: String,
    val appName: String,
    val usedMinutes: Int,
    val limitMinutes: Int,
    val overLimit: Boolean = false,
)
