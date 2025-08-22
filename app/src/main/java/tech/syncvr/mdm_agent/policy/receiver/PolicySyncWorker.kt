package tech.syncvr.mdm_agent.policy.receiver

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.syncvr.mdm_agent.R
import tech.syncvr.mdm_agent.policy.policy.PolicyRepository
import java.util.concurrent.TimeUnit

class PolicySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repo = PolicyRepository(context, R.raw.mdm_policy)
    private val json = Json { prettyPrint = true }

    override suspend fun doWork(): Result {
        val policy = repo.loadPolicy()
        val policyJson = json.encodeToString(policy)

        // broadcast send config
        val intent = Intent(ACTION_POLICY_UPDATE).apply {
            setPackage(applicationContext.packageName)
            putExtra(EXTRA_POLICY_JSON, policyJson)
        }
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }

    companion object {
        const val ACTION_POLICY_UPDATE = "com.example.mdm_agent.POLICY_UPDATE"
        const val EXTRA_POLICY_JSON = "policy_json"
    }
}

object PolicySyncScheduler {

    private const val WORK_NAME = "PolicySync"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<PolicySyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
