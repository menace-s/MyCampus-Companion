package com.example.mycampuscompanion.ui.features.reporting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddReportScreen(navController: NavController) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // On crée une variable pour stocker l'URI temporaire, avant la prise de photo
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // C'EST ICI QUE LA LOGIQUE CHANGE
            if (success) {
                // La photo a été prise et sauvegardée avec succès.
                // MAINTENANT, on met à jour l'état qui contrôle l'affichage.
                imageUri = tempImageUri
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // La permission est accordée, on lance la caméra
                val newUri = createImageUri(context)
                tempImageUri = newUri // On stocke la nouvelle URI
                cameraLauncher.launch(newUri)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Nouveau Signalement", style = MaterialTheme.typography.headlineSmall)

        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
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

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                    // La permission est déjà là, on lance la caméra
                    val newUri = createImageUri(context)
                    tempImageUri = newUri // On stocke l'URI en attente
                    cameraLauncher.launch(newUri)
                }
                else -> {
                    // On demande la permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }) {
            Text(if (imageUri == null) "Prendre une photo" else "Prendre une autre photo")
        }

        Button(
            onClick = { /* TODO: Sauvegarder le signalement */ },
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