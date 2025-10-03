package com.example.mycampuscompanion.ui.features.reporting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

@Composable
fun AddReportScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Nouveau Signalement")

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Prend le plus de place possible en hauteur
        )

        Button(onClick = { /* TODO: Lancer l'appareil photo */ }) {
            Text("Prendre une photo / vidéo")
        }

        Button(onClick = { /* TODO: Sauvegarder le signalement */ }) {
            Text("Envoyer le signalement")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AddReportScreenPreview() {
//    MyCampusCompanionTheme {
//        AddReportScreen()
//    }
//}