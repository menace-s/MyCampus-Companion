package com.example.mycampuscompanion.ui.features.reporting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

@Composable
fun ReportingListScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // On dit au navigateur d'aller à l'adresse "add_report"
                    navController.navigate("add_report")
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un signalement")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Liste des signalements (à venir)")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ReportingListScreenPreview() {
//    MyCampusCompanionTheme {
//        ReportingListScreen()
//    }
//}