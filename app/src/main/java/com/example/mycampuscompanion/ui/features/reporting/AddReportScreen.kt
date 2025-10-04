package com.example.mycampuscompanion.ui.features.reporting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import android.widget.VideoView
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
import androidx.compose.ui.viewinterop.AndroidView
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
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore



@Composable
fun AddReportScreen(
    navController: NavController,
    reportingViewModel: ReportingViewModel
) {
    // --- GESTION DE L'ÉTAT ---
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var mediaUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isVideo by rememberSaveable { mutableStateOf(false) }
    var tempMediaUri by remember { mutableStateOf<Uri?>(null) }
    var hasAttemptedSubmit by rememberSaveable { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    // --- LAUNCHERS ---
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                mediaUri = tempMediaUri
                isVideo = false
            }
        }
    )

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mediaUri = tempMediaUri
                isVideo = true
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission accordée. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission accordée. Veuillez cliquer à nouveau sur 'Envoyer'.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // --- DIALOGUE DE PERMISSION ---
    if (showCameraPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showCameraPermissionDialog = false },
            title = { Text("Permission pour la caméra requise") },
            text = { Text("Pour joindre un média, l'application a besoin d'accéder à votre caméra.") },
            confirmButton = {
                Button(onClick = {
                    showCameraPermissionDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Continuer") }
            },
            dismissButton = {
                Button(onClick = { showCameraPermissionDialog = false }) { Text("Annuler") }
            }
        )
    }

    // --- INTERFACE UTILISATEUR ---
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Nouveau Signalement", style = MaterialTheme.typography.headlineSmall)

        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            if (mediaUri != null) {
                if (isVideo) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(mediaUri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = mediaUri),
                        contentDescription = "Aperçu de l'image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Text("Aucun média capturé", textAlign = TextAlign.Center)
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = {
                handleMediaCapture(context, "photo", mediaUri, cameraPermissionLauncher) { uri ->
                    tempMediaUri = uri
                    photoLauncher.launch(uri)
                }
            }) { Text("Prendre une photo") }

            Button(modifier = Modifier.weight(1f), onClick = {handleMediaCapture(context, "video", mediaUri, cameraPermissionLauncher) { uri ->
                tempMediaUri = uri
                // On crée un Intent personnalisé
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri) // On dit où sauvegarder
                    putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30) // Limite de 30 secondes
                }
                videoLauncher.launch(intent)
            }
            }) { Text("Vidéo (30s max)") }
        }

        Button(
            onClick = {
                hasAttemptedSubmit = true
                if (title.isNotBlank() && description.isNotBlank() && mediaUri != null) {
                    when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        PackageManager.PERMISSION_GRANTED -> {
                            reportingViewModel.saveReport(title, description, mediaUri!!, isVideo) {
                                navController.popBackStack()
                            }
                        }
                        else -> {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            },
            enabled = mediaUri != null
        ) {
            Text("Envoyer le signalement")
        }
    }
}

private fun handleMediaCapture(
    context: Context,
    type: String,
    currentMediaUri: Uri?,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onPermissionGranted: (Uri) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        currentMediaUri?.let { oldUri ->
            context.contentResolver.delete(oldUri, null, null)
        }
        val newUri = if (type == "video") createVideoUri(context) else createImageUri(context)
        onPermissionGranted(newUri)
    } else {
        // Gérer l'affichage du dialogue d'explication si on voulait l'ajouter ici aussi
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(context.cacheDir, "images/photo_$timeStamp.jpg")
    imageFile.parentFile?.mkdirs()
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
}

private fun createVideoUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val videoFile = File(context.cacheDir, "videos/video_$timeStamp.mp4")
    videoFile.parentFile?.mkdirs()
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)
}