package com.example.mycampuscompanion.ui.features.reporting

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycampuscompanion.data.local.AppDatabase
import com.example.mycampuscompanion.data.model.Report
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

// Ce ViewModel gère MAINTENANT la liste ET l'ajout
class ReportingViewModel(application: Application) : AndroidViewModel(application) {

    private val reportDao = AppDatabase.getInstance(application).reportDao()
    private val locationClient = LocationServices.getFusedLocationProviderClient(application)

    // --- Logique de ReportingListViewModel ---
    val reports: StateFlow<List<Report>> = reportDao.getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Logique de ReportingViewModel ---
    fun saveReport(title: String, description: String, imageUri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val hasPermission = ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                try {
                    val location = locationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).await()

                    val report = Report(
                        title = title,
                        description = description,
                        imageUri = imageUri.toString(),
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    reportDao.insert(report)
                    Log.d("VIEWMODEL_DEBUG", "✅ Signalement sauvegardé ! Titre: ${report.title}")
                    onSuccess() // Appelle le callback de succès

                } catch (e: Exception) {
                    Log.e("VIEWMODEL_DEBUG", "❌ Erreur de sauvegarde: ${e.message}")
                }
            } else {
                println("Permission de localisation non accordée.")
            }
        }
    }
}