package com.mobile_systems.android.movienight

import android.app.Application
import com.mobile_systems.android.movienight.data.AppContainer
import com.mobile_systems.android.movienight.data.AppDataContainer

class MovieNightApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}