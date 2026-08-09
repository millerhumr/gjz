package com.parentguard.child.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.parentguard.child.ChildApp
import com.parentguard.child.R
import com.parentguard.child.data.DataStorePrefs
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 位置上报服务
 * 定时上报孩子位置到云端
 */
class LocationService : Service() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(NOTI_ID, buildNoti())
        requestLocationUpdates()
    }

    @Suppress("MissingPermission")
    private fun requestLocationUpdates() {
        if (!hasPermission()) return

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60_000L)
            .setMinUpdateIntervalMillis(30_000L)
            .build()

        fusedClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                uploadLocation(loc)
            }
        }, Looper.getMainLooper())
    }

    private fun uploadLocation(loc: Location) {
        scope.launch {
            try {
                val token = DataStorePrefs.getDeviceToken(applicationContext) ?: return@launch
                val serverBase = com.parentguard.child.BuildConfig.SYNC_BASE_URL
                val payload = """{"lat":${loc.latitude},"lng":${loc.longitude},"address":""}"""
                val req = Request.Builder()
                    .url("$serverBase/api/location")
                    .header("X-Device-Token", token)
                    .post(payload.toRequestBody("application/json".toMediaType()))
                    .build()
                client.newCall(req).execute().close()
            } catch (_: Exception) {}
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun buildNoti(): Notification {
        return NotificationCompat.Builder(this, ChildApp.CHANNEL_MONITOR)
            .setContentTitle("家长助手·位置守护中")
            .setContentText("正在定时上报位置")
            .setSmallIcon(R.drawable.ic_guard)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTI_ID = 1002

        fun start(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LocationService::class.java))
        }
    }
}
