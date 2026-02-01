package com.mobile_systems.android.movienight

import android.app.Application
import com.mobile_systems.android.movienight.data.AppContainer
import com.mobile_systems.android.movienight.data.AppDataContainer

/*
* Runs only once when the app is started.
* It provides dependencies to the rest of the app.
*/
class MovieNightApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}