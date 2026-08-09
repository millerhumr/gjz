# 家长助手 Android 项目

> 完整可编译的 Android Studio 项目源码 · Kotlin + Jetpack Compose · 双 Module

## 项目结构

```
parent-guard-android/
├── README.md                    # 本文件
├── BUILD.md                     # 详细构建指南
├── settings.gradle.kts          # 模块配置
├── build.gradle.kts             # 根 Gradle
├── gradle.properties            # Gradle 配置
├── gradle/wrapper/              # Gradle Wrapper
│
├── app/                         # 家长端 Module
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/parentguard/parent/
│       │   ├── ParentApp.kt              # Application
│       │   ├── MainActivity.kt           # 主页
│       │   ├── ui/
│       │   │   ├── theme/                # Compose 主题
│       │   │   ├── screens/              # 各个页面
│       │   │   └── components/           # 复用组件
│       │   ├── data/
│       │   │   ├── model/                # 数据模型
│       │   │   ├── repository/           # 数据仓库
│       │   │   └── remote/               # 配对通信
│       │   └── viewmodel/
│       └── res/                          # 资源文件
│
└── child/                       # 孩子端 Module
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/parentguard/child/
        │   ├── ChildApp.kt
        │   ├── MainActivity.kt
        │   ├── service/
        │   │   ├── UsageMonitorService.kt          # UsageStats 监听
        │   │   ├── GuardAccessibilityService.kt     # 无障碍拦截
        │   │   ├── LocationService.kt              # 位置上报
        │   │   └── CommandReceiver.kt              # 家长命令接收
        │   ├── launcher/                           # 启动器
        │   ├── data/
        │   └── viewmodel/
        └── res/
```

## 核心功能模块

| 模块 | Android 实现 |
|---|---|
| 时长统计 | `UsageStatsManager` + 前台 Service 监听 |
| 应用拦截 | `AccessibilityService` + 启动器限制 |
| 设备管理 | `DevicePolicyManager` 锁屏/卸载保护 |
| 位置上报 | `FusedLocationProviderClient` 定时上传 |
| 家长通信 | `WebSocket` / `MQTT` 长连接（轻量实现用 HTTP 轮询） |
| 番茄钟 | 孩子端本地 Service + 状态机 |
| 反卸载 | 设备管理员 + 隐藏图标 + `ACTION_PACKAGE_REMOVED` 监听 |

## 编译要求

- Android Studio Hedgehog | 2023.1.1 或更高
- JDK 17
- Android SDK 34
- Kotlin 1.9.22
- 最低运行：Android 8.0 (API 26)
- 推荐运行：Android 12+ (API 31+)

## 编译步骤

```bash
# 1. 用 Android Studio 打开本目录
# 2. 等待 Gradle Sync 完成
# 3. Build → Build Bundle(s)/APK(s) → Build APK(s)
# 4. 生成的 APK 在 app/build/outputs/apk/ 和 child/build/outputs/apk/
```

详细步骤、签名、权限申请流程见 `BUILD.md`。
