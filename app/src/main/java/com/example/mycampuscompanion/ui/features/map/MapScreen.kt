package com.example.mycampuscompanion.ui.features.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// On crée une Factory pour notre MapViewModel, comme pour le NewsViewModel
object MapViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel(factory = MapViewModelFactory)) {
    val context = LocalContext.current
    val state by mapViewModel.state.collectAsState()

    // Le gestionnaire pour la demande de permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // La permission a été accordée, on récupère la position
                mapViewModel.getUserLocation()
            }
        }
    )

    // Se déclenche une seule fois au lancement de l'écran
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // La permission est déjà accordée
                mapViewModel.getUserLocation()
            }
            else -> {
                // On demande la permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    val esaticLibraryPoint = GeoPoint(5.290718, -3.998327)

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(17.0)
                controller.setCenter(esaticLibraryPoint)

                // Marqueur pour la bibliothèque
                val poiMarker = Marker(this)
                poiMarker.position = esaticLibraryPoint
                poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                poiMarker.title = "Bibliothèque de l'ESATIC"
                this.overlays.add(poiMarker)
            }
        },
        update = { mapView ->
            // Ce bloc est appelé chaque fois que l'état change
            state.userLocation?.let { userLocation ->
                // On centre la carte sur l'utilisateur
                mapView.controller.animateTo(userLocation)

                // On ajoute un overlay spécial pour la position de l'utilisateur
                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                locationOverlay.enableMyLocation()
                mapView.overlays.add(locationOverlay)
            }
            mapView.invalidate()
        }
    )
}