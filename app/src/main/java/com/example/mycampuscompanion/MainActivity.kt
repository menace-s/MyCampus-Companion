package com.example.mycampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mycampuscompanion.data.model.Contact
import com.example.mycampuscompanion.ui.features.directory.AnnuaireScreen
import com.example.mycampuscompanion.ui.features.news.NewsScreen
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
//                    NewsScreen()
                    AnnuaireScreen(contacts = sampleContacts)
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