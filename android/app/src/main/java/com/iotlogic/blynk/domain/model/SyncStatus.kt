package com.iotlogic.blynk.domain.model

/**
 * Synchronization status enumeration
 */
enum class SyncStatus {
    IDLE,           // No sync operation in progress
    SYNCING,        // Sync operation in progress
    COMPLETED,      // Last sync completed successfully
    ERROR,          // Last sync failed with error
    PARTIAL,        // Sync partially completed (some operations succeeded, others failed)
    SYNCED,         // Data is synced with server
    FAILED,         // Sync operation failed
    PENDING         // Sync operation is pending
}