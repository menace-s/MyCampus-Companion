package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.mycampuscompanion.data.model.Contact
import com.example.mycampuscompanion.ui.features.directory.AnnuaireScreen
import com.example.mycampuscompanion.ui.features.news.NewsScreen
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCampusCompanionTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 1. Initialiser le contrôleur de navigation
    val navController = rememberNavController()

    // La liste des écrans pour notre barre de navigation
    val items = listOf(
        Screen.Actualites,
        Screen.Annuaire,
        Screen.Carte,
        Screen.Signalement
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 2. Mettre en place le NavHost qui affichera les écrans
        NavHost(navController, startDestination = Screen.Actualites.route, Modifier.padding(innerPadding)) {
            composable(Screen.Actualites.route) { NewsScreen() }
            composable(Screen.Annuaire.route) { AnnuaireScreen(contacts = sampleContacts) }
            composable(Screen.Carte.route) { com.example.mycampuscompanion.ui.features.map.MapScreen() }
            navigation(
                startDestination = "reporting_list", // L'écran de départ de cette section
                route = Screen.Signalement.route     // La route principale pour cette section
            ) {
                // La "rue" pour l'écran de la liste
                composable("reporting_list") {
                    com.example.mycampuscompanion.ui.features.reporting.ReportingListScreen(navController)
                }
                // La "rue" pour l'écran du formulaire
                composable("add_report") {
                    com.example.mycampuscompanion.ui.features.reporting.AddReportScreen(navController)
                }
            }
        }
    }
}


// Liste de données pour la démo (on la laisse ici temporairement)
val sampleContacts = listOf(
    Contact(1, "Aganh", "Jean", "05 44 83 35 50", "jean.dupont@campus.com"),
    Contact(2, "Durand", "Marie", "02 34 56 78 90"),
    Contact(3, "Martin", "Pierre", "03 45 67 89 01", "pierre.martin@campus.com"),
)