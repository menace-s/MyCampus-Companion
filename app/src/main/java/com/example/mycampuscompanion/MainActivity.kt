package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.mycampuscompanion.data.local.AppDatabase
import com.example.mycampuscompanion.data.remote.RetrofitClient
import com.example.mycampuscompanion.data.repository.NewsRepository
import com.example.mycampuscompanion.ui.features.directory.AnnuaireScreen
import com.example.mycampuscompanion.ui.features.news.NewsScreen
import com.example.mycampuscompanion.ui.features.news.NewsViewModel
import com.example.mycampuscompanion.ui.features.reporting.ReportingViewModel
import com.example.mycampuscompanion.ui.features.reporting.ReportingViewModelFactory
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration du Repository pour les actualités NYTimes
        val database = AppDatabase.getInstance(applicationContext)
        val newsRepository = NewsRepository(
            apiService = RetrofitClient.apiService,
            postDao = database.postDao(),
            apiKey = "8kK1GqsOz27BVkXClvc953BY2pB8ctf8" // ⚠️ Remplacez par votre clé NYTimes API
        )

        setContent {
            MyCampusCompanionTheme {
                MainScreen(newsRepository = newsRepository)
            }
        }
    }
}

@Composable
fun MainScreen(newsRepository: NewsRepository) {
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

            // ÉCRAN ACTUALITÉS avec ViewModel et Repository
            composable(Screen.Actualites.route) {
                val newsViewModel: NewsViewModel = viewModel(
                    factory = NewsViewModelFactory(newsRepository)
                )
                NewsScreen(viewModel = newsViewModel)
            }

            composable(Screen.Annuaire.route) { AnnuaireScreen() }

            composable(Screen.Carte.route) {
                com.example.mycampuscompanion.ui.features.map.MapScreen()
            }

            // GRAPHE DE NAVIGATION POUR LE SIGNALEMENT
            navigation(
                startDestination = "reporting_list",
                route = Screen.Signalement.route
            ) {
                composable("reporting_list") { backStackEntry ->
                    // 1. On trouve le "propriétaire" du ViewModel : le graphe de navigation "Signalement"
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Signalement.route)
                    }
                    // 2. On crée le ViewModel en le liant à ce propriétaire parent.
                    val reportingViewModel: ReportingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = ReportingViewModelFactory
                    )

                    // 3. On passe ce ViewModel partagé à notre écran.
                    com.example.mycampuscompanion.ui.features.reporting.ReportingListScreen(
                        navController = navController,
                        viewModel = reportingViewModel
                    )
                }

                composable("add_report") { backStackEntry ->
                    // On fait EXACTEMENT la même chose pour le deuxième écran
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Signalement.route)
                    }
                    val reportingViewModel: ReportingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = ReportingViewModelFactory
                    )

                    com.example.mycampuscompanion.ui.features.reporting.AddReportScreen(
                        navController = navController,
                        reportingViewModel = reportingViewModel
                    )
                }
            }
        }
    }
}

// Factory pour créer le NewsViewModel avec injection du Repository
class NewsViewModelFactory(
    private val repository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}