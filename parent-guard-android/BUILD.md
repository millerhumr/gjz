# 家长助手 Android · 构建指南

> 从源码到可安装 APK 的完整步骤

## 一、准备开发环境

### 1. 安装 Android Studio

- 下载 [Android Studio Hedgehog | 2023.1.1](https://developer.android.com/studio) 或更新版本
- 安装时勾选：Android SDK、Android SDK Platform 34、Android Virtual Device

### 2. 配置 JDK 17

Android Studio 自带 JBR 17，无需额外配置。如需手动安装：

```bash
# Windows
winget install Microsoft.OpenJDK.17

# 验证
java -version
# 应显示 openjdk version "17.x.x"
```

### 3. 设置 Android SDK

`File → Settings → Languages & Frameworks → Android SDK`：
- ✅ Android 14.0 (API 34)
- ✅ Android 8.0 (API 26)  // 最低支持
- ✅ Android SDK Build-Tools 34.0.0
- ✅ Android SDK Platform-Tools
- ✅ Android SDK Command-line Tools

## 二、导入项目

```bash
# 1. 启动 Android Studio
# 2. File → Open → 选择 parent-guard-android 目录
# 3. 等待 Gradle Sync 完成（首次需要下载依赖，可能 5-15 分钟）
# 4. 看到 "BUILD SUCCESSFUL" 表示配置成功
```

## 三、生成调试版 APK（开发测试用）

### 家长端

```bash
# 方式1：命令行
cd parent-guard-android
./gradlew :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk

# 方式2：Android Studio
# 右侧 Gradle → app → Tasks → build → assembleDebug
```

### 孩子端

```bash
./gradlew :child:assembleDebug
# 产物：child/build/outputs/apk/debug/child-debug.apk
```

### 双端一键打包

```bash
./gradlew assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
#       child/build/outputs/apk/debug/child-debug.apk
```

## 四、安装到真机

### 方式1：ADB 安装

```bash
# 1. 手机开启 USB 调试（设置 → 关于手机 → 连续点击"版本号"7次 → 返回 → 开发者选项 → USB调试）
# 2. 用数据线连接电脑
# 3. 验证连接
adb devices
# 4. 安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r child/build/outputs/apk/debug/child-debug.apk
```

### 方式2：Android Studio 直接 Run

```
1. 选择 module（app 或 child）
2. 选择连接的设备
3. 点击 ▶️ Run
```

## 五、生成发布版 APK（可对外发布）

### 1. 生成签名密钥

```bash
keytool -genkey -v -keystore parent-guard.keystore -alias parent-guard \
        -keyalg RSA -keysize 2048 -validity 10000
# 记住设置的密码！会用于后续签名
```

### 2. 配置签名

在 `~/.gradle/gradle.properties` 添加：

```properties
PG_KEYSTORE=/path/to/parent-guard.keystore
PG_KEY_ALIAS=parent-guard
PG_KEY_PASSWORD=你的密码
PG_STORE_PASSWORD=你的密码
```

修改 `app/build.gradle.kts` 和 `child/build.gradle.kts` 的 buildTypes.release：

```kotlin
release {
    signingConfig = signingConfigs.create("release") {
        storeFile = file(System.getenv("PG_KEYSTORE") ?: "parent-guard.keystore")
        storePassword = System.getenv("PG_STORE_PASSWORD")
        keyAlias = System.getenv("PG_KEY_ALIAS")
        keyPassword = System.getenv("PG_KEY_PASSWORD")
    }
    isMinifyEnabled = true
    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
}
```

### 3. 生成 Release APK

```bash
./gradlew assembleRelease
# 产物带签名，可直接安装分发
```

## 六、孩子端首次运行必须授予的权限

打开孩子端 APP，会引导用户授权以下权限，**全部必须给**：

| 权限 | 用途 | 路径 |
|---|---|---|
| **使用情况访问权限** | 统计每个 APP 使用时长 | 设置 → 隐私 → 使用情况访问 → 家长助手 |
| **无障碍服务** | 拦截孩子打开被禁应用 | 设置 → 无障碍 → 已下载服务 → 家长助手守护服务 |
| **设备管理员** | 防止孩子卸载本 APP | 弹窗确认 → 激活 |
| **位置权限** | 实时位置与足迹 | 弹窗 → 始终允许 |
| **后台运行** | 防被杀，保证守护生效 | 设置 → 电池 → 不受限制 |
| **自启动** | 开机自启 | 设置 → 应用 → 自启动 → 家长助手 |
| **悬浮窗** | 顶层弹窗提示 | 设置 → 应用 → 特殊权限 → 显示在其他应用上层 |

**关键：**
- 华为/小米/OPPO/vivo 等国产 ROM 都有"自启动管理"和"电池优化"，必须手动加白名单
- 部分 ROM（华为 EMUI）会"智能杀后台"，需要把 APP 锁定到最近任务栏

## 七、家长端与孩子端配对

### 1. 启动家长端

打开家长端 APP → 我的设备 → 点击右上角"+ 添加"

### 2. 显示二维码 + 6位配对码

```
┌──────────────┐
│  ▓▓ ▓ ▓▓▓▓  │
│  ▓ ▓▓▓ ▓ ▓  │  ← 二维码
│  ▓▓▓ ▓ ▓ ▓  │
│              │
│  8 4 2 5 6 1 │  ← 配对码
└──────────────┘
```

### 3. 孩子端扫码

打开孩子端 → 启动页 → "扫一扫"扫描家长端二维码 / "输入配对码"输入 6 位数字。

### 4. 自动绑定

- 服务端生成 device_token 存到孩子端 DataStore
- 孩子端每 5 分钟向服务端上传使用数据
- 家长端从服务端拉取使用数据 / 下发规则

> ⚠️ **本项目不包含服务端代码**，需要自己搭建。最简方案：
> - 用 LeanCloud / Bmob / Firebase 做云端
> - 或自建 FastAPI / Spring Boot 服务

## 八、常见问题

### Q1: Gradle Sync 失败
- 解决：File → Invalidate Caches → Restart
- 检查网络能否访问 maven.google.com
- 必要时配置代理或镜像

### Q2: 编译时报 "Cannot find symbol"  
- 解决：Build → Clean Project → Rebuild Project
- 检查 Kotlin 版本是否 1.9.22

### Q3: 孩子端被杀后台
- 解决：
  - 锁定最近任务栏
  - 关闭电池优化
  - 华为/小米"自启动"开白名单
  - OPPO/vivo "后台冻结"关闭

### Q4: 无障碍服务被系统关闭
- 解决：每次重启后引导用户重新开启（可加入自检逻辑）

### Q5: 如何调试无障碍服务
```bash
adb shell settings put secure enabled_accessibility_services \
    com.parentguard.child/com.parentguard.child.service.GuardAccessibilityService
```

### Q6: 拦截失败的场景
- 部分游戏有"反检测"机制（如外挂检测），可能拒绝运行
- 解决方案：使用 `setComponentEnabledSetting` 隐藏本 APP 图标
- 或使用 `DevicePolicyManager.setPackagesSuspended()` 暂停目标应用（Android 11+）

## 九、架构说明

### 数据流向

```
家长端 APP
   ↓ 下发规则
云端服务器 (自建)
   ↑ 上报使用
   ↓ 拉取命令
孩子端守护服务 (UsageMonitor + AccessibilityService)
   ↓ 拦截
BlockedActivity
```

### 核心类

| 类 | 职责 |
|---|---|
| `GuardEngine` | 决策中心：是否拦截某 APP |
| `GuardAccessibilityService` | 监听窗口变化，触发拦截 |
| `UsageMonitorService` | 前台 Service，30s 轮询 UsageStats |
| `DataStorePrefs` | 规则本地持久化 |
| `SyncAgent` | 与云端同步数据 |
| `AdminReceiver` | 设备管理员，防卸载 |
| `LauncherActivity` | 启动器，替代默认桌面 |

### 拦截策略

1. **第一道防线**：无障碍服务监听窗口变化（实时，< 100ms）
2. **第二道防线**：UsageMonitor 30s 检测，超时触发拦截
3. **第三道防线**：启动器隐藏被禁 APP 图标
4. **可选第四道**：`DevicePolicyManager.setPackagesSuspended`（Android 11+）

## 十、发布到应用市场

调试版 APK 可以直接安装，但**不建议**直接发布到 Google Play / 华为应用市场，因为：
- 家长控制类 APP 需要 Google 特殊审核
- 华为/小米等需要隐私合规材料
- 设备管理员权限会触发"危险权限"警告

**建议发布策略：**
- 企业内部分发（私有部署）
- 或申请 Google 的 [Family Link API](https://developers.google.com/child-devices) 做合规化
- 国内：申请《儿童隐私保护》合规认证

---

**有任何问题，把错误日志贴给我，我帮你看。** 祝你构建顺利 🎉
