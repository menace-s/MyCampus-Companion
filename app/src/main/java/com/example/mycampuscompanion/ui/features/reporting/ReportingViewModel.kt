package com.example.mycampuscompanion.ui.features.reporting

import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mycampuscompanion.data.LocationRepository
import com.example.mycampuscompanion.data.ReportRepository
import com.example.mycampuscompanion.data.local.AppDatabase
import com.example.mycampuscompanion.data.model.Report
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- MODIFICATION : Le ViewModel ne dépend plus que du Repository ---
class ReportingViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // On observe les données via le Repository
    val reports: StateFlow<List<Report>> = reportRepository.getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // La fonction de sauvegarde délègue tout le travail au Repository
    fun saveReport(title: String, description: String, imageUri: Uri, isVideo: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                reportRepository.insert(title, description, imageUri, isVideo)
                onSuccess()
            } catch (e: Exception) {
                // Gérer les erreurs remontées par le Repository (ex: permission refusée)
                println("Erreur interceptée par le ViewModel : ${e.message}")
            }
        }
    }
}

// Dans ReportingViewModel.kt

// La classe ReportingViewModel ne change pas...

object ReportingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        if (modelClass.isAssignableFrom(ReportingViewModel::class.java)) {
            // 1. On construit TOUTES les dépendances nécessaires
            val reportDao = AppDatabase.getInstance(application).reportDao()
            val locationRepository = LocationRepository(application)
            val reportRepository = ReportRepository(reportDao, locationRepository)

            @Suppress("UNCHECKED_CAST")
            // 2. On injecte le ReportRepository dans le ViewModel
            return ReportingViewModel(reportRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}