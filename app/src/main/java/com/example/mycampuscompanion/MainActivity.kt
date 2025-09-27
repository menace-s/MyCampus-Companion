package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Pour récupérer le Context
import com.example.mycampuscompanion.ui.theme.MyCampusCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCampusCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsScreen()
//                    AnnuaireScreen(contacts = sampleContacts)
                }
            }
        }
    }
}
// Liste de données pour la démo
val sampleContacts = listOf(
    Contact(1, "Aganh", "Jean", "05 44 83 35 50", "jean.dupont@campus.com"),
    Contact(2, "Durand", "Marie", "02 34 56 78 90"),
    Contact(3, "Martin", "Pierre", "03 45 67 89 01", "pierre.martin@campus.com"),
    Contact(4, "Bernard", "Alice", "04 56 78 90 12"),
    Contact(5, "Thomas", "Lucie", "05 67 89 01 23", "lucie.thomas@campus.com")
)


@Composable
fun AnnuaireScreen(contacts: List<Contact>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(contacts) { contact ->
            ContactCard(contact = contact)
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {

    // 1. On récupère le contexte actuel
    val context = LocalContext.current
    // Row est le conteneur principal pour aligner les éléments horizontalement
    Row(
        modifier = Modifier
            .fillMaxWidth() // La rangée prend toute la largeur
            .padding(vertical = 8.dp, horizontal = 16.dp), // Un peu d'espace
        verticalAlignment = Alignment.CenterVertically // Centre les éléments verticalement
    ) {
        // Column pour le nom et le numéro (prend toute la place restante)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${contact.prenom} ${contact.nom}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = contact.numeroDeTelephone,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // --- C'EST ICI QU'ON AJOUTE L'INTERACTIVITÉ ---

        // Bouton d'icône pour l'appel
        IconButton(onClick = {
            // 2. Créer l'intention de COMPOSER un numéro (ACTION_DIAL)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.numeroDeTelephone}"))
            // 3. Lancer l'activité correspondante
            context.startActivity(intent)
//            println("DEBUG: Appel de ${contact.prenom}")
        }) {
            Icon(
                imageVector = Icons.Default.Call, // L'icône du téléphone
                contentDescription = "Appeler ${contact.prenom}" // Pour l'accessibilité
            )
        }

        // Bouton d'icône pour le SMS
        IconButton(onClick = {
            // 2. Créer l'intention d'ENVOYER un message (ACTION_SENDTO)
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.numeroDeTelephone}"))
            // On peut même pré-remplir le message !
//            intent.putExtra("sms_body", "Bonjour ${contact.prenom}, ")
            // 3. Lancer l'activité
            context.startActivity(intent)
        }) {
            Icon(
                imageVector = Icons.Default.Send, // L'icône d'envoi
                contentDescription = "Envoyer un SMS à ${contact.prenom}"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnuaireScreenPreview() {
    MyCampusCompanionTheme {
//        AnnuaireScreen(contacts = sampleContacts)
        NewsScreen()
    }
}