package com.parentguard.parent.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    private const val BASE = "http://122.51.108.88"
    var authToken: String? = null

    private suspend fun get(path: String): JSONObject = withContext(Dispatchers.IO) {
        val conn = URL(BASE + path).openConnection() as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer $authToken")
        val text = if (conn.responseCode in 200..299)
            conn.inputStream.bufferedReader().readText()
        else
            conn.errorStream.bufferedReader().readText()
        conn.disconnect()
        JSONObject(if (text.isBlank()) "{}" else text)
    }

    private suspend fun post(path: String, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val conn = URL(BASE + path).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.outputStream.write(body.toString().toByteArray())
        val text = if (conn.responseCode in 200..299)
            conn.inputStream.bufferedReader().readText()
        else
            conn.errorStream.bufferedReader().readText()
        conn.disconnect()
        JSONObject(if (text.isBlank()) "{}" else text)
    }

    suspend fun login(username: String, password: String): JSONObject {
        return post("/api/login", JSONObject().put("username", username).put("password", password))
    }

    suspend fun register(username: String, password: String): JSONObject {
        return post("/api/register", JSONObject().put("username", username).put("password", password))
    }

    suspend fun getDevices(): JSONObject {
        return get("/api/devices")
    }
}
