package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import com.example.mycampuscompanion.ui.features.map.MapScreen
import com.example.mycampuscompanion.ui.features.news.NewsScreen
import com.example.mycampuscompanion.ui.features.news.NewsViewModel
import com.example.mycampuscompanion.ui.features.reporting.AddReportScreen
import com.example.mycampuscompanion.ui.features.reporting.ReportingListScreen
import com.example.mycampuscompanion.ui.features.reporting.ReportingViewModel
import com.example.mycampuscompanion.ui.features.reporting.ReportingViewModelFactory
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val newsRepository = NewsRepository(
            apiService = RetrofitClient.apiService,
            postDao = database.postDao(),
            apiKey = "8kK1GqsOz27BVkXClvc953BY2pB8ctf8"
        )

        setContent {
            MyCampusCompanionTheme {
                MainScreen(newsRepository = newsRepository)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(newsRepository: NewsRepository) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Actualites,
        Screen.Annuaire,
        Screen.Carte,
        Screen.Signalement
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSurface,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Actualites.route,
            Modifier.padding(innerPadding),

            // 🎨 ANIMATIONS AMÉLIORÉES - Suit l'ordre des index de Screen
            enterTransition = {
                // Récupérer les index depuis la classe Screen
                val initialIndex = when (initialState.destination.route) {
                    Screen.Actualites.route -> 0
                    Screen.Annuaire.route -> 1
                    Screen.Carte.route -> 2
                    Screen.Signalement.route, "reporting_list", "add_report" -> 3
                    else -> 0
                }
                val targetIndex = when (targetState.destination.route) {
                    Screen.Actualites.route -> 0
                    Screen.Annuaire.route -> 1
                    Screen.Carte.route -> 2
                    Screen.Signalement.route, "reporting_list", "add_report" -> 3
                    else -> 0
                }

                when {
                    // Navigation vers la droite (index augmente) : 0→1→2→3
                    targetIndex > initialIndex -> {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(300))
                    }
                    // Navigation vers la gauche (index diminue) : 3→2→1→0
                    targetIndex < initialIndex -> {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(300))
                    }
                    // Même écran ou sous-navigation
                    else -> fadeIn(animationSpec = tween(300))
                }
            },

            exitTransition = {
                val initialIndex = when (initialState.destination.route) {
                    Screen.Actualites.route -> 0
                    Screen.Annuaire.route -> 1
                    Screen.Carte.route -> 2
                    Screen.Signalement.route, "reporting_list", "add_report" -> 3
                    else -> 0
                }
                val targetIndex = when (targetState.destination.route) {
                    Screen.Actualites.route -> 0
                    Screen.Annuaire.route -> 1
                    Screen.Carte.route -> 2
                    Screen.Signalement.route, "reporting_list", "add_report" -> 3
                    else -> 0
                }

                when {
                    // Navigation vers la droite
                    targetIndex > initialIndex -> {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    // Navigation vers la gauche
                    targetIndex < initialIndex -> {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    else -> fadeOut(animationSpec = tween(300))
                }
            },

            // Animation pour les sous-écrans (reporting)
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },

            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            // 📰 ÉCRAN ACTUALITÉS (Index 0)
            composable(
                route = Screen.Actualites.route
            ) {
                val newsViewModel: NewsViewModel = viewModel(factory = NewsViewModelFactory(newsRepository))
                NewsScreen(viewModel = newsViewModel)
            }

            // 📞 ÉCRAN ANNUAIRE (Index 1)
            composable(
                route = Screen.Annuaire.route
            ) {
                AnnuaireScreen()
            }

            // 🗺️ ÉCRAN CARTE (Index 2)
            composable(
                route = Screen.Carte.route
            ) {
                MapScreen()
            }

            // 📸 GRAPHE SIGNALEMENT (Index 3) - avec animations verticales pour les sous-écrans
            navigation(
                startDestination = "reporting_list",
                route = Screen.Signalement.route
            ) {
                composable(
                    route = "reporting_list",
                    enterTransition = {
                        // Retour depuis add_report : slide vertical
                        if (initialState.destination.route == "add_report") {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(400)
                            ) + fadeIn(tween(300))
                        } else {
                            // Utilise l'animation globale du NavHost
                            null
                        }
                    },
                    exitTransition = {
                        // Vers add_report : slide vertical
                        if (targetState.destination.route == "add_report") {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(400)
                            ) + fadeOut(tween(300))
                        } else {
                            // Utilise l'animation globale du NavHost
                            null
                        }
                    }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Signalement.route)
                    }
                    val reportingViewModel: ReportingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = ReportingViewModelFactory
                    )
                    ReportingListScreen(navController = navController, viewModel = reportingViewModel)
                }

                composable(
                    route = "add_report",
                    enterTransition = {
                        // Entre depuis reporting_list : slide de bas en haut
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        ) + fadeIn(tween(300))
                    },
                    exitTransition = {
                        // Sort vers reporting_list : slide de haut en bas
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        ) + fadeOut(tween(300))
                    },
                    popEnterTransition = {
                        // Animation de retour (back button)
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        ) + fadeIn(tween(300))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        ) + fadeOut(tween(300))
                    }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Signalement.route)
                    }
                    val reportingViewModel: ReportingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = ReportingViewModelFactory
                    )
                    AddReportScreen(navController = navController, reportingViewModel = reportingViewModel)
                }
            }
        }
    }
}

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