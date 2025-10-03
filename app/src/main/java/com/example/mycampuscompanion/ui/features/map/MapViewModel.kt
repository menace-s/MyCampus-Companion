package com.example.mycampuscompanion.ui.features.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

data class MapState(
    val userLocation: GeoPoint? = null
)

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    // Le client pour accéder aux services de localisation
    private val locationClient = LocationServices.getFusedLocationProviderClient(application)

    fun getUserLocation() {
        // Vérifier si la permission a bien été accordée
        val hasPermission = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // Si oui, on demande la dernière position connue
            locationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    _state.value = _state.value.copy(
                        userLocation = GeoPoint(it.latitude, it.longitude)
                    )
                }
            }
        }
    }
}