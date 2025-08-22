package tech.syncvr.mdm_agent.policy.policy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Policy(
    val wifiDirect: WifiDirectConfig,
    val dataChannel: DataChannelConfig
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class WifiDirectConfig(
    val enabled: Boolean,
    val discoveryBackoffSeconds: List<Int>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class DataChannelConfig(
    val heartbeatIntervalSec: Int,
    val maxMisses: Int
)

@SuppressLint("UnsafeOptInUsageError")
class PolicyRepository(
    private val context: Context,
    @RawRes private val defaultPolicyRes: Int
) {

    private val prefs = context.getSharedPreferences("mdm_policy", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun loadPolicy(): Policy {
        // prvo probaj da čitaš sdcard
        val externalFile = File("/sdcard/Download/mdm_policy.json")
        val policyJson = try {
            if (externalFile.exists()) externalFile.readText()
            else readRaw(defaultPolicyRes)
        } catch (e: Exception) {
            Log.e("PolicyRepo", "Failed to read external policy, using last valid", e)
            return loadLastValidPolicy() ?: parseDefault()
        }

        return try {
            val parsed = json.decodeFromString<Policy>(policyJson)
            val hash = sha256(policyJson)
            saveLastValidPolicy(policyJson, hash)
            parsed
        } catch (e: Exception) {
            Log.e("PolicyRepo", "Failed to parse policy, fallback to last valid", e)
            loadLastValidPolicy() ?: parseDefault()
        }
    }

    private fun saveLastValidPolicy(policyJson: String, hash: String) {
        prefs.edit()
            .putString("last_policy", policyJson)
            .putString("last_hash", hash)
            .apply()
    }

    private fun loadLastValidPolicy(): Policy? {
        val policyJson = prefs.getString("last_policy", null) ?: return null
        return runCatching { json.decodeFromString<Policy>(policyJson) }.getOrNull()
    }

    private fun parseDefault(): Policy {
        val defaultJson = readRaw(defaultPolicyRes)
        return json.decodeFromString(defaultJson)
    }

    private fun readRaw(@RawRes res: Int): String =
        context.resources.openRawResource(res).bufferedReader().use { it.readText() }

    private fun sha256(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(text.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
