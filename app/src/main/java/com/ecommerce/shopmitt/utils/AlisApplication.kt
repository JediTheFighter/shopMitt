package com.ecommerce.shopmitt.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import com.ecommerce.shopmitt.db.AppDatabase
import com.shopping.alis.MyEventBusIndex
import org.greenrobot.eventbus.EventBus

class AlisApplication : Application(), ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var isAppIsForeGround = false

    val navigator: Navigator by lazy { Navigator.getNavigator(this) }

    val database by lazy { AppDatabase.getDatabase(this) }
    val cartRepo by lazy { database.cartDao() }



    companion object {
        var context: Context? = null
        lateinit var instance: AlisApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

        EventBus.builder() // have a look at the index class to see which methods are picked up
            // if not in the index @Subscribe methods will be looked up at runtime (expensive)
            .addIndex(MyEventBusIndex())
            .installDefaultEventBus()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(p0: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            isAppIsForeGround = true
        }
    }

    override fun onActivityResumed(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(p0: Activity) {
        isActivityChangingConfigurations = p0.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
            isAppIsForeGround = false
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(p0: Activity) {
        TODO("Not yet implemented")
    }

    fun isAppIsForeGround(): Boolean {
        LogHelper.instance.printDebugLog("printWarningLog : $isAppIsForeGround")
        return isAppIsForeGround
    }

    /*public void setConnectivityListener(NetworkSchedulerService.ConnectivityReceiverListener listener) {
        NetworkSchedulerService.connectivityReceiverListener = listener;
    }*/

}