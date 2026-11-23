package com.zahranur.mapdroid

import android.app.Application
import org.maplibre.android.MapLibre

/**
 * Application class for Mapdroid.
 * Initializes MapLibre Native SDK with application context.
 */
class MapdroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize MapLibre Native SDK once for the lifecycle of the application
        MapLibre.getInstance(this)
    }
}
