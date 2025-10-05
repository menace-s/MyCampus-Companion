// ui/features/news/NewsViewModel.kt
package com.example.mycampuscompanion.ui.features.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'écran des actualités
 * Gère la logique de présentation et l'état de l'UI
 */
class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    // État de l'UI (privé et mutable)
    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)

    // État exposé à la Vue (public et immuable)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        // Charger les actualités au démarrage
        loadNews()
    }

    /**
     * Charge les actualités depuis le repository
     */
    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading

            repository.getNews().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { posts ->
                        NewsUiState.Success(posts)
                    },
                    onFailure = { error ->
                        NewsUiState.Error(error.message ?: "Erreur inconnue")
                    }
                )
            }
        }
    }

    /**
     * Rafraîchit les actualités (appelé par le bouton refresh)
     */
    fun refresh() {
        loadNews()
    }

    /**
     * Charge les actualités d'une section spécifique
     * @param section Section NYTimes (world, technology, science, etc.)
     */
    fun loadNewsBySection(section: String) {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading

            repository.getNewsBySection(section).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { posts ->
                        NewsUiState.Success(posts)
                    },
                    onFailure = { error ->
                        NewsUiState.Error(error.message ?: "Erreur inconnue")
                    }
                )
            }
        }
    }
}

/**
 * États possibles de l'écran des actualités
 */
sealed class NewsUiState {
    // Chargement en cours
    object Loading : NewsUiState()

    // Données chargées avec succès
    data class Success(val posts: List<Post>) : NewsUiState()

    // Erreur lors du chargement
    data class Error(val message: String) : NewsUiState()
}