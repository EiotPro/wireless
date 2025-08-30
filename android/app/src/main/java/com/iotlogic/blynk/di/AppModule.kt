package com.iotlogic.blynk.di

import android.content.Context
import androidx.room.Room
import com.iotlogic.blynk.data.local.IoTLogicDatabase
import com.iotlogic.blynk.data.local.dao.CommandQueueDao
import com.iotlogic.blynk.data.local.dao.ConfigurationDao
import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.dao.TelemetryDao
import com.iotlogic.blynk.data.remote.ApiClient
import com.iotlogic.blynk.data.remote.ApiService
import com.iotlogic.blynk.data.repository.DeviceRepositoryImpl
import com.iotlogic.blynk.data.repository.TelemetryRepositoryImpl
import com.iotlogic.blynk.data.repository.ConfigurationRepositoryImpl
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.domain.repository.TelemetryRepository
import com.iotlogic.blynk.domain.repository.ConfigurationRepository
import com.iotlogic.blynk.hardware.HardwareManager
import com.iotlogic.blynk.hardware.bluetooth.BluetoothLeManager
import com.iotlogic.blynk.hardware.mqtt.MqttConnectionManager
import com.iotlogic.blynk.hardware.usb.UsbSerialManager
import com.iotlogic.blynk.hardware.wifi.WiFiDeviceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideBluetoothLeManager(
        @ApplicationContext context: Context
    ): BluetoothLeManager {
        return BluetoothLeManager(context)
    }
    
    @Provides
    @Singleton
    fun provideWiFiDeviceManager(
        @ApplicationContext context: Context
    ): WiFiDeviceManager {
        return WiFiDeviceManager(context)
    }
    
    @Provides
    @Singleton
    fun provideUsbSerialManager(
        @ApplicationContext context: Context
    ): UsbSerialManager {
        return UsbSerialManager(context)
    }
    
    @Provides
    @Singleton
    fun provideMqttConnectionManager(
        @ApplicationContext context: Context
    ): MqttConnectionManager {
        return MqttConnectionManager(context)
    }
    
    @Provides
    @Singleton
    fun provideHardwareManager(
        @ApplicationContext context: Context,
        bluetoothLeManager: BluetoothLeManager,
        wifiDeviceManager: WiFiDeviceManager,
        usbSerialManager: UsbSerialManager,
        mqttConnectionManager: MqttConnectionManager
    ): HardwareManager {
        return HardwareManager(
            context,
            bluetoothLeManager,
            wifiDeviceManager,
            usbSerialManager,
            mqttConnectionManager
        )
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.100/blynk/") // Default local development URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideApiClient(apiService: ApiService): ApiClient {
        return ApiClient(apiService)
    }
    
    @Provides
    @Singleton
    fun provideIoTLogicDatabase(
        @ApplicationContext context: Context
    ): IoTLogicDatabase {
        return Room.databaseBuilder(
            context,
            IoTLogicDatabase::class.java,
            "iotlogic_database"
        )
            .addMigrations(IoTLogicDatabase.MIGRATION_1_2)
            .addCallback(IoTLogicDatabase.prepopulateCallback)
            .build()
    }
    
    @Provides
    fun provideDeviceDao(database: IoTLogicDatabase) = database.deviceDao()
    
    @Provides
    fun provideTelemetryDao(database: IoTLogicDatabase) = database.telemetryDao()
    
    @Provides
    fun provideConfigurationDao(database: IoTLogicDatabase) = database.configurationDao()
    
    @Provides
    fun provideCommandQueueDao(database: IoTLogicDatabase) = database.commandQueueDao()
    
    @Provides
    @Singleton
    fun provideDeviceRepository(
        deviceDao: DeviceDao,
        apiClient: ApiClient
    ): DeviceRepository {
        return DeviceRepositoryImpl(deviceDao, apiClient)
    }
    
    @Provides
    @Singleton
    fun provideTelemetryRepository(
        telemetryDao: TelemetryDao,
        apiClient: ApiClient
    ): TelemetryRepository {
        return TelemetryRepositoryImpl(telemetryDao, apiClient)
    }
    
    @Provides
    @Singleton
    fun provideConfigurationRepository(
        configurationDao: ConfigurationDao,
        apiClient: ApiClient
    ): ConfigurationRepository {
        return ConfigurationRepositoryImpl(configurationDao, apiClient)
    }
}