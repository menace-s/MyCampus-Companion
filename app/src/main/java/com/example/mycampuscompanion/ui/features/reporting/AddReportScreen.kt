package com.example.mycampuscompanion.ui.features.reporting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Composable
fun AddReportScreen(
    navController: NavController,
    reportingViewModel: ReportingViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasAttemptedSubmit by rememberSaveable { mutableStateOf(false) }

    // --- AJOUT : État pour contrôler la visibilité de notre dialogue d'explication ---
    var showCameraPermissionDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) { imageUri = tempImageUri } }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // La permission a été accordée via le dialogue système, on lance la caméra
                val newUri = createImageUri(context)
                tempImageUri = newUri
                cameraLauncher.launch(newUri)
            }
        }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission accordée. Veuillez cliquer à nouveau sur 'Envoyer'.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Permission de localisation refusée.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // --- AJOUT : Le Composable pour notre boîte de dialogue d'explication ---
    if (showCameraPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showCameraPermissionDialog = false },
            title = { Text("Permission pour la caméra requise") },
            text = { Text("Pour ajouter une photo à votre signalement, l'application a besoin d'accéder à votre caméra.") },
            confirmButton = {
                Button(onClick = {
                    showCameraPermissionDialog = false
                    // On lance la demande de permission système
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Continuer") }
            },
            dismissButton = {
                Button(onClick = { showCameraPermissionDialog = false }) { Text("Annuler") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Nouveau Signalement", style = MaterialTheme.typography.headlineSmall)

        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Aperçu de l'image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Aucune image capturée", textAlign = TextAlign.Center)
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isBlank() && hasAttemptedSubmit,
            supportingText = { if (title.isBlank() && hasAttemptedSubmit) { Text("Ce champ ne peut pas être vide") } }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            isError = description.isBlank() && hasAttemptedSubmit,
            supportingText = { if (description.isBlank() && hasAttemptedSubmit) { Text("Ce champ ne peut pas être vide") } }
        )

        Button(onClick = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                    imageUri?.let { oldUri -> context.contentResolver.delete(oldUri, null, null) }
                    val newUri = createImageUri(context)
                    tempImageUri = newUri
                    cameraLauncher.launch(newUri)
                }
                else -> {
                    // --- MODIFICATION : On affiche notre dialogue au lieu de lancer directement la permission ---
                    showCameraPermissionDialog = true
                }
            }
        }) {
            Text(if (imageUri == null) "Prendre une photo" else "Prendre une autre photo")
        }

        Button(
            onClick = {
                hasAttemptedSubmit = true
                if (title.isNotBlank() && description.isNotBlank() && imageUri != null) {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                            reportingViewModel.saveReport(title, description, imageUri!!) {
                                navController.popBackStack()
                            }
                        }
                        else -> {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            },
            enabled = imageUri != null
        ) {
            Text("Envoyer le signalement")
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(context.cacheDir, "images/photo_$timeStamp.jpg")
    imageFile.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}