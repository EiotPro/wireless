package com.iotlogic.blynk.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iotlogic.blynk.data.local.dao.CommandQueueDao
import com.iotlogic.blynk.data.local.dao.ConfigurationDao
import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.dao.TelemetryDao
import com.iotlogic.blynk.data.local.entities.CommandQueueEntity
import com.iotlogic.blynk.data.local.entities.ConfigurationEntity
import com.iotlogic.blynk.data.local.entities.DeviceEntity
import com.iotlogic.blynk.data.local.entities.TelemetryEntity

@Database(
    entities = [
        DeviceEntity::class,
        TelemetryEntity::class,
        ConfigurationEntity::class,
        CommandQueueEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IoTLogicDatabase : RoomDatabase() {
    
    abstract fun deviceDao(): DeviceDao
    abstract fun telemetryDao(): TelemetryDao
    abstract fun configurationDao(): ConfigurationDao
    abstract fun commandQueueDao(): CommandQueueDao
    
    companion object {
        const val DATABASE_NAME = "iotlogic_database"
        
        // Migration from version 1 to 2 - Add command queue table
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create command_queue table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `command_queue` (
                        `id` TEXT NOT NULL,
                        `deviceId` TEXT NOT NULL,
                        `command` TEXT NOT NULL,
                        `parameters` TEXT NOT NULL,
                        `priority` INTEGER NOT NULL DEFAULT 0,
                        `status` TEXT NOT NULL DEFAULT 'PENDING',
                        `retryCount` INTEGER NOT NULL DEFAULT 0,
                        `maxRetries` INTEGER NOT NULL DEFAULT 3,
                        `result` TEXT,
                        `errorMessage` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `scheduledAt` INTEGER,
                        `sentAt` INTEGER,
                        `completedAt` INTEGER,
                        `expiresAt` INTEGER,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`deviceId`) REFERENCES `devices`(`id`) ON DELETE CASCADE
                    )
                """)
                
                // Create indices for command_queue table
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_deviceId` ON `command_queue` (`deviceId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_status` ON `command_queue` (`status`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_priority` ON `command_queue` (`priority`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_createdAt` ON `command_queue` (`createdAt`)")
            }
        }
        
        // Prepopulate callback for default configurations
        val prepopulateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // You can prepopulate the database here if needed
                // This is useful for default configurations or reference data
            }
        }
    }
}