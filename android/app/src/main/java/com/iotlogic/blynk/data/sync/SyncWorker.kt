package com.iotlogic.blynk.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iotlogic.blynk.data.local.preferences.AuthPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val authPreferences: AuthPreferences
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get auth token
            val token = authPreferences.getAuthToken()
            
            if (token == null) {
                // No auth token available, skip sync
                return Result.success()
            }
            
            // Perform sync
            val syncResult = syncManager.startFullSync(token)
            
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                // Retry on failure
                Result.retry()
            }
        } catch (e: Exception) {
            // Retry on exception
            Result.retry()
        }
    }
}