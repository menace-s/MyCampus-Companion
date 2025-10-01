package com.example.mycampuscompanion.data.model

data class Contact(
    val id: Int,
    val nom: String,
    val prenom: String,
    val numeroDeTelephone: String,
    val email: String? = null // Le '?' signifie que l'email est optionnel (peut être nul)
)