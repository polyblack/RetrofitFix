package com.polyblack.retrofitfix

import android.app.Application
import timber.log.Timber

class RetrofitFixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // TODO Remove or add custom logic
                }
            })
        }
    }
}