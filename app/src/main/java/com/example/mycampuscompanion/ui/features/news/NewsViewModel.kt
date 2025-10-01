package com.example.mycampuscompanion.ui.features.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mycampuscompanion.data.local.AppDatabase
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// Représente les différents états possibles de notre écran d'actualités
sealed interface NewsState {
    object Loading : NewsState
    data class Success(val posts: List<Post>) : NewsState
    data class Error(val message: String) : NewsState
}

// 1. On hérite maintenant de AndroidViewModel pour avoir accès au Contexte
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // 2. On récupère une instance de notre DAO (la porte d'accès à la base de données)
    private val postDao = AppDatabase.getInstance(application).postDao()

    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)
    val state: StateFlow<NewsState> = _state

    init {
        // 3. On lance DEUX coroutines en parallèle

        // Tâche n°1 : Observer la base de données EN CONTINU
        viewModelScope.launch {
            postDao.getAllPosts() // Récupère le Flow de la base de données
                .catch { e -> // S'il y a une erreur en lisant la base de données
                    _state.value = NewsState.Error("Erreur de base de données: ${e.message}")
                }
                .collect { posts -> // Chaque fois que la base de données change...
                    // ... on met à jour l'état de l'UI avec les nouvelles données.
                    _state.value = NewsState.Success(posts)
                }
        }

        // Tâche n°2 : Rafraîchir les données depuis le réseau UNE SEULE FOIS
        refreshPosts()
    }

    private fun refreshPosts() {
        viewModelScope.launch {
            try {
                // On va chercher les articles "frais" sur le réseau
                val freshPosts = RetrofitClient.apiService.getPosts()
                // Si ça réussit, on les insère dans la base de données
                postDao.insertAll(freshPosts)
                // C'est tout ! On ne met PAS à jour l'état de l'UI ici.
                // La Tâche n°1 (l'observateur) s'en chargera automatiquement !
            } catch (e: Exception) {
                // S'il y a une erreur réseau, on ne fait rien de spécial.
                // L'utilisateur continuera simplement de voir les données
                // qui étaient déjà dans la base de données. On pourrait afficher
                // un petit message d'erreur temporaire si on voulait.
                println("Erreur réseau: ${e.message}")
            }
        }
    }
}