package com.example.mycampuscompanion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val index: Int) {
    object Actualites : Screen("actualites", "Actualités", Icons.Default.Home, 0)
    object Annuaire : Screen("annuaire", "Annuaire", Icons.Default.List, 1)
    object Carte : Screen("carte", "Carte", Icons.Default.LocationOn, 2)
    object Signalement : Screen("signalement", "Signaler", Icons.Default.PhotoCamera, 3)
}