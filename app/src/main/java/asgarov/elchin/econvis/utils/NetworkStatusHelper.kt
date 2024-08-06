package asgarov.elchin.econvis.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class NetworkStatusHelper private constructor() {

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    private var previousStatus: Boolean? = null

    fun setNetworkStatus(isOnline: Boolean) {
        if (previousStatus == null || previousStatus != isOnline) {
            Log.d("NetworkStatusHelper", "setNetworkStatus: previousStatus=$previousStatus, isOnline=$isOnline")
            _networkStatus.value = isOnline
            previousStatus = isOnline
            Log.d("NetworkStatusHelper", "setNetworkStatus: previousStatus updated to $previousStatus")
        }
    }

    fun getPreviousNetworkStatus(): Boolean? {
        return previousStatus
    }

    companion object {
        val instance: NetworkStatusHelper by lazy { NetworkStatusHelper() }
    }
}