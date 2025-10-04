package com.example.mycampuscompanion.data

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationRepository(private val application: Application) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(application)

    suspend fun getUserLocation(): Location? {
        val hasPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Si la permission n'est pas là, on ne peut rien faire.
            // On pourrait lancer une exception, mais retourner null est plus simple à gérer.
            return null
        }

        // On demande la position actuelle avec une haute priorité
        return locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
    }
}