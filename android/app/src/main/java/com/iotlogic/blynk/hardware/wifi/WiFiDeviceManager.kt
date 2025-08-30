package com.iotlogic.blynk.hardware.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages WiFi operations including network scanning, connection, and HTTP device discovery
 */
@Singleton
class WiFiDeviceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    
    // State flows
    private val _scanResults = MutableSharedFlow<List<WiFiNetwork>>()
    val scanResults: SharedFlow<List<WiFiNetwork>> = _scanResults.asSharedFlow()
    
    private val _discoveredDevices = MutableSharedFlow<WiFiDevice>()
    val discoveredDevices: SharedFlow<WiFiDevice> = _discoveredDevices.asSharedFlow()
    
    private val _connectionState = MutableStateFlow(WiFiConnectionState.DISCONNECTED)
    val connectionState: StateFlow<WiFiConnectionState> = _connectionState.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val discoveredNetworkDevices = mutableMapOf<String, WiFiDevice>()
    
    /**
     * Initialize WiFi manager
     */
    suspend fun initialize(): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (!wifiManager.isWifiEnabled) {
                    Result.failure(Exception("WiFi is not enabled"))
                } else {
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Start scanning for WiFi networks
     */
    suspend fun scanNetworks(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!hasWiFiPermissions()) {
                    return@withContext Result.failure(Exception("Missing WiFi permissions"))
                }
                
                _isScanning.value = true
                
                val success = wifiManager.startScan()
                if (success) {
                    delay(3000) // Wait for scan to complete
                    
                    val scanResults = wifiManager.scanResults
                    val wifiNetworks = scanResults.map { result ->
                        WiFiNetwork(
                            ssid = result.SSID,
                            bssid = result.BSSID,
                            capabilities = result.capabilities,
                            level = result.level,
                            frequency = result.frequency,
                            timestamp = result.timestamp,
                            isSecure = result.capabilities.contains("WPA") || result.capabilities.contains("WEP")
                        )
                    }.distinctBy { it.ssid }
                    
                    _scanResults.emit(wifiNetworks)
                    _isScanning.value = false
                    
                    Result.success(Unit)
                } else {
                    _isScanning.value = false
                    Result.failure(Exception("Failed to start WiFi scan"))
                }
            } catch (e: SecurityException) {
                _isScanning.value = false
                Result.failure(Exception("Missing WiFi permissions: ${e.message}"))
            } catch (e: Exception) {
                _isScanning.value = false
                Result.failure(e)
            }
        }
    }
    
    /**
     * Connect to a WiFi network (Android 10+)
     */
    suspend fun connectToNetwork(ssid: String, password: String? = null): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val specifier = WifiNetworkSpecifier.Builder()
                        .setSsid(ssid)
                        .apply {
                            password?.let { setWpa2Passphrase(it) }
                        }
                        .build()
                    
                    val request = NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .setNetworkSpecifier(specifier)
                        .build()
                    
                    val networkCallback = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            _connectionState.value = WiFiConnectionState.CONNECTED
                        }
                        
                        override fun onLost(network: Network) {
                            _connectionState.value = WiFiConnectionState.DISCONNECTED
                        }
                        
                        override fun onUnavailable() {
                            _connectionState.value = WiFiConnectionState.FAILED
                        }
                    }
                    
                    _connectionState.value = WiFiConnectionState.CONNECTING
                    connectivityManager.requestNetwork(request, networkCallback)
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Network connection requires Android 10+"))
                }
            } catch (e: Exception) {
                _connectionState.value = WiFiConnectionState.FAILED
                Result.failure(e)
            }
        }
    }
    
    /**
     * Discover HTTP devices on the local network
     */
    suspend fun discoverHttpDevices(
        portRange: IntRange = 80..8080,
        commonPorts: List<Int> = listOf(80, 8080, 443, 8443, 3000, 5000)
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val localIp = getLocalIpAddress()
                    ?: return@withContext Result.failure(Exception("Could not determine local IP address"))
                
                val subnet = localIp.substringBeforeLast(".") + "."
                val discoveryJobs = mutableListOf<Job>()
                
                // Scan common IP range (192.168.x.1 to 192.168.x.254)
                for (i in 1..254) {
                    val ip = "$subnet$i"
                    
                    val job = launch {
                        scanHostForHttpServices(ip, commonPorts)
                    }
                    discoveryJobs.add(job)
                }
                
                // Wait for all discovery jobs to complete
                discoveryJobs.joinAll()
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Send HTTP command to a discovered device
     */
    suspend fun sendHttpCommand(
        deviceIp: String,
        port: Int,
        endpoint: String,
        method: String = "GET",
        body: String? = null,
        headers: Map<String, String> = emptyMap()
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "http://$deviceIp:$port$endpoint"
                val requestBuilder = Request.Builder().url(url)
                
                // Add headers
                headers.forEach { (key, value) ->
                    requestBuilder.addHeader(key, value)
                }
                
                // Set request body for POST/PUT
                when (method.uppercase()) {
                    "POST", "PUT" -> {
                        val requestBody = RequestBody.create(
                            "application/json; charset=utf-8".toMediaType(),
                            body ?: ""
                        )
                        requestBuilder.method(method, requestBody)
                    }
                    else -> requestBuilder.method(method, null)
                }
                
                val request = requestBuilder.build()
                val response = httpClient.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("HTTP request failed with code: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get current WiFi connection info
     */
    fun getCurrentWiFiInfo(): WiFiConnectionInfo? {
        return try {
            if (!hasWiFiPermissions()) return null
            
            val wifiInfo = wifiManager.connectionInfo
            WiFiConnectionInfo(
                ssid = wifiInfo.ssid?.removeSurrounding("\"") ?: "",
                bssid = wifiInfo.bssid ?: "",
                rssi = wifiInfo.rssi,
                linkSpeed = wifiInfo.linkSpeed,
                frequency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    wifiInfo.frequency
                } else {
                    -1
                },
                ipAddress = wifiInfo.ipAddress
            )
        } catch (e: SecurityException) {
            null
        }
    }
    
    /**
     * Get discovered devices
     */
    fun getDiscoveredDevices(): List<WiFiDevice> {
        return discoveredNetworkDevices.values.toList()
    }
    
    /**
     * Check if device is reachable
     */
    suspend fun pingDevice(ipAddress: String, timeout: Int = 3000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val address = InetAddress.getByName(ipAddress)
                address.isReachable(timeout)
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Shutdown WiFi manager
     */
    suspend fun shutdown() {
        scope.cancel()
        httpClient.dispatcher.executorService.shutdown()
    }
    
    private suspend fun scanHostForHttpServices(ip: String, ports: List<Int>) {
        for (port in ports) {
            try {
                val socket = Socket()
                val address = InetSocketAddress(ip, port)
                
                withTimeout(2000) {
                    withContext(Dispatchers.IO) {
                        socket.connect(address, 1000)
                        socket.close()
                    }
                }
                
                // Port is open, try to get device info via HTTP
                val deviceInfo = getDeviceInfo(ip, port)
                
                val device = WiFiDevice(
                    ipAddress = ip,
                    port = port,
                    hostname = deviceInfo?.hostname ?: "Unknown",
                    deviceType = deviceInfo?.deviceType ?: "HTTP Device",
                    lastSeen = System.currentTimeMillis(),
                    services = listOf("HTTP"),
                    isReachable = true
                )
                
                discoveredNetworkDevices[ip] = device
                _discoveredDevices.emit(device)
                
            } catch (e: Exception) {
                // Port is closed or unreachable
            }
        }
    }
    
    private suspend fun getDeviceInfo(ip: String, port: Int): DeviceInfo? {
        return try {
            val response = sendHttpCommand(ip, port, "/", "GET")
            if (response.isSuccess) {
                // Try to parse device information from response
                // This is a simplified version - real implementation would parse various formats
                DeviceInfo(
                    hostname = ip,
                    deviceType = "IoT Device"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getLocalIpAddress(): String? {
        return try {
            val wifiInfo = wifiManager.connectionInfo
            val ipInt = wifiInfo.ipAddress
            String.format(
                "%d.%d.%d.%d",
                ipInt and 0xff,
                ipInt shr 8 and 0xff,
                ipInt shr 16 and 0xff,
                ipInt shr 24 and 0xff
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun hasWiFiPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}

/**
 * Represents a WiFi network
 */
data class WiFiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val level: Int,
    val frequency: Int,
    val timestamp: Long,
    val isSecure: Boolean
)

/**
 * Represents a discovered WiFi device
 */
data class WiFiDevice(
    val ipAddress: String,
    val port: Int,
    val hostname: String,
    val deviceType: String,
    val lastSeen: Long,
    val services: List<String>,
    val isReachable: Boolean
)

/**
 * WiFi connection information
 */
data class WiFiConnectionInfo(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val linkSpeed: Int,
    val frequency: Int,
    val ipAddress: Int
)

/**
 * Device information
 */
data class DeviceInfo(
    val hostname: String,
    val deviceType: String
)

/**
 * WiFi connection states
 */
enum class WiFiConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED
}