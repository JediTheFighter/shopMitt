

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.ecommerce.shopmitt.utils.AlisApplication

class NetworkUtil private constructor() {

    fun hasNetworkConnection(): Boolean {
        val connectivityManager = AlisApplication.instance
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private object HOLDER {
        val INSTANCE = NetworkUtil()
    }

    companion object {
        //val instance: NetworkUtil = HOLDER.INSTANCE
        val instance: NetworkUtil by lazy { HOLDER.INSTANCE }
    }

}