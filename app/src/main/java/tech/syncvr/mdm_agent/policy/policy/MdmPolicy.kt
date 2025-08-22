package tech.syncvr.mdm_agent.policy.policy

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MdmPolicy(
    val wifiDirect: WifiDirectPolicy,
    val dataChannel: DataChannelPolicy
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class WifiDirectPolicy(
    val enabled: Boolean,
    val discoveryBackoffSeconds: List<Int>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class DataChannelPolicy(
    val heartbeatIntervalSec: Int,
    val maxMisses: Int
)