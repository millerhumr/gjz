package com.parentguard.parent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parentguard.parent.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ParentViewModel(app: Application) : AndroidViewModel(app) {
    private val _token = MutableStateFlow<String?>(null); val token: StateFlow<String?> = _token
    private val _loading = MutableStateFlow(false); val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null); val error: StateFlow<String?> = _error
    private val _devices = MutableStateFlow<List<JSONObject>>(emptyList()); val devices: StateFlow<List<JSONObject>> = _devices
    private val _selectedDevice = MutableStateFlow<JSONObject?>(null); val selectedDevice: StateFlow<JSONObject?> = _selectedDevice
    private val _usage = MutableStateFlow<List<JSONObject>>(emptyList()); val usage: StateFlow<List<JSONObject>> = _usage
    private val _pendingRequests = MutableStateFlow<List<JSONObject>>(emptyList()); val pendingRequests: StateFlow<List<JSONObject>> = _pendingRequests
    private val _pairCode = MutableStateFlow(""); val pairCode: StateFlow<String> = _pairCode
    private val _location = MutableStateFlow<JSONObject?>(null); val location: StateFlow<JSONObject?> = _location

    fun clearError() { _error.value = null }
    fun login(u: String, p: String) { viewModelScope.launch { _loading.value = true; _error.value = null; try { val r = ApiClient.login(u, p); _token.value = r.getString("token"); ApiClient.authToken = r.getString("token"); } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false } } }
    fun register(u: String, p: String) { viewModelScope.launch { _loading.value = true; _error.value = null; try { val r = ApiClient.register(u, p); _token.value = r.getString("token"); ApiClient.authToken = r.getString("token"); } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false } } }
    fun loadDevices() { viewModelScope.launch { try { val arr = ApiClient.getDevices().getJSONArray("devices"); _devices.value = (0 until arr.length()).map { arr.getJSONObject(it) } } catch (_: Exception) {} } }
    fun generatePairCode(nm: String, mdl: String) { viewModelScope.launch { _loading.value = true; try { _pairCode.value = ApiClient.generatePairCode(nm, mdl).getString("pair_code") } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false } } }
    fun selectDevice(d: JSONObject) { _selectedDevice.value = d }
    fun loadUsage(did: Int) { viewModelScope.launch { try { val arr = ApiClient.getUsage(did).getJSONArray("usage"); _usage.value = (0 until arr.length()).map { arr.getJSONObject(it) } } catch (_: Exception) {} } }
    fun loadPendingRequests() { viewModelScope.launch { try { val arr = ApiClient.getPendingRequests().getJSONArray("requests"); _pendingRequests.value = (0 until arr.length()).map { arr.getJSONObject(it) } } catch (_: Exception) {} } }
    fun approveRequest(rid: Int, ok: Boolean) { viewModelScope.launch { try { ApiClient.approveRequest(rid, ok); loadPendingRequests() } catch (_: Exception) {} } }
    fun saveRules(did: Int, payload: JSONObject) { viewModelScope.launch { try { ApiClient.updateRules(did, payload) } catch (_: Exception) {} } }
    fun loadLocation(did: Int) { viewModelScope.launch { try { val r = ApiClient.getLocation(did); if (r.has("location") && !r.isNull("location")) _location.value = r.getJSONObject("location") } catch (_: Exception) {} } }
}
