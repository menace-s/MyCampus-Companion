package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    AnnuaireScreen(contacts = sampleContacts)
                }
            }
        }
    }
}
// Liste de données pour la démo
val sampleContacts = listOf(
    Contact(1, "Dupont", "Jean", "01 23 45 67 89", "jean.dupont@campus.com"),
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
    // Pour l'instant, on affiche juste le nom complet et le numéro
    Column(modifier = Modifier.padding(16.dp)) { // Column permet d'empiler les éléments verticalement
        Text(
            text = "${contact.prenom} ${contact.nom}",
            style = MaterialTheme.typography.titleMedium // On utilise le style du thème !
        )
        Text(
            text = contact.numeroDeTelephone,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnnuaireScreenPreview() {
    MyCampusCompanionTheme {
        AnnuaireScreen(contacts = sampleContacts)
    }
}