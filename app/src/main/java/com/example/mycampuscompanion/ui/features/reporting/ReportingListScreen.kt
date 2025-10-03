package com.example.mycampuscompanion.ui.features.reporting

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mycampuscompanion.data.model.Report

// La Factory n'est plus dans ce fichier, c'est parfait.

@Composable
fun ReportingListScreen(
    navController: NavController,
    viewModel: ReportingViewModel
) {
    Log.d("VIEWMODEL_DEBUG", "🧠 ViewModel dans l'écran LISTE: ${viewModel.hashCode()}")
    val reports by viewModel.reports.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_report") }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un signalement")
            }
        }
    ) { innerPadding ->
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Aucun signalement. Cliquez sur '+' pour en ajouter un.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reports) { report ->
                    // --- MODIFICATION : On passe une action de clic à la ReportCard ---
                    ReportCard(
                        report = report,
                        onClick = {
                            // TODO: Naviguer vers l'écran de détail avec l'ID du rapport
                            println("Clicked on report with id: ${report.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    report: Report,
    onClick: () -> Unit // --- AJOUT : La Card accepte maintenant une fonction onClick ---
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // --- AJOUT : On rend la Card cliquable ---
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = report.imageUri.toUri(),
                contentDescription = report.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = report.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = report.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}