package com.example.mycampuscompanion.ui.features.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mycampuscompanion.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

data class MapState(
    val userLocation: GeoPoint? = null
)

class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun getUserLocation() {
        viewModelScope.launch {
            val location = locationRepository.getUserLocation()
            location?.let {
                _state.value = _state.value.copy(
                    userLocation = GeoPoint(it.latitude, it.longitude)
                )
            }
        }
    }
}

object MapViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            // La Factory construit le Repository et l'injecte dans le ViewModel
            val locationRepository = LocationRepository(application)
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}