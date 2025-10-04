package com.example.mycampuscompanion.data

import android.net.Uri
import com.example.mycampuscompanion.data.local.ReportDao
import com.example.mycampuscompanion.data.model.Report

class ReportRepository(
    private val reportDao: ReportDao,
    // Il dépend maintenant du LocationRepository
    private val locationRepository: LocationRepository
) {

    fun getAllReports() = reportDao.getAllReports()

    suspend fun insert(title: String, description: String, imageUri: Uri,isVideo: Boolean) {
        // On demande la localisation au spécialiste
        val location = locationRepository.getUserLocation()

        // On s'assure qu'on a bien reçu une localisation avant de sauvegarder
        if (location != null) {
            val report = Report(
                title = title,
                description = description,
                imageUri = imageUri.toString(),
                latitude = location.latitude,
                longitude = location.longitude,
                isVideo = isVideo
            )
            reportDao.insert(report)
        } else {
            // Si la localisation a échoué (ex: permission refusée), on lance une exception
            throw Exception("Impossible d'obtenir la localisation pour le signalement.")
        }
    }
}