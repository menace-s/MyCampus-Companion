package com.example.mycampuscompanion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Annuaire : Screen("annuaire", "Annuaire", Icons.Default.List)
    object Actualites : Screen("actualites", "Actualités", Icons.Default.Home)
    object Carte : Screen("carte", "Carte", Icons.Default.LocationOn)
    object Signalement : Screen("signalement", "Signaler", Icons.Default.PhotoCamera)
}