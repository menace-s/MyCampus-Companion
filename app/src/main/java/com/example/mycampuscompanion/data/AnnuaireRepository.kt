package com.example.mycampuscompanion.data

import android.content.Context
import com.example.mycampuscompanion.data.model.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnnuaireRepository(private val context: Context) {

    suspend fun getContacts(): List<Contact> {
        // La lecture de fichier est une opération "lourde", on la fait en arrière-plan
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = context.assets.open("contacts.json")
                    .bufferedReader()
                    .use { it.readText() }

                val listType = object : TypeToken<List<Contact>>() {}.type
                Gson().fromJson(jsonString, listType)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // En cas d'erreur, on retourne une liste vide
            }
        }
    }
}