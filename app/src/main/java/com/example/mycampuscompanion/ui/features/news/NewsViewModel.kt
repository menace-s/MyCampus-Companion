package com.example.mycampuscompanion.ui.features.news

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mycampuscompanion.data.NewsRepository
import com.example.mycampuscompanion.data.local.AppDatabase
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface NewsState {
    object Loading : NewsState
    data class Success(val posts: List<Post>) : NewsState
    data class Error(val message: String) : NewsState
}

// --- Le ViewModel ne dépend plus que du Repository ---
class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)
    val state: StateFlow<NewsState> = _state

    init {
        // On observe le flux de données venant du Repository
        viewModelScope.launch {
            newsRepository.getPosts()
                .catch { e ->
                    _state.value = NewsState.Error("Erreur de base de données: ${e.message}")
                }
                .collect { posts ->
                    _state.value = NewsState.Success(posts)
                }
        }

        // On demande au Repository de rafraîchir les données
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            newsRepository.refreshPosts()
        }
    }
}

// --- La Factory se charge de construire le Repository et de l'injecter ---
object NewsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            // 1. On crée toutes les dépendances ici
            val postDao = AppDatabase.getInstance(application).postDao()
            val apiService = RetrofitClient.apiService
            val repository = NewsRepository(apiService, postDao)

            @Suppress("UNCHECKED_CAST")
            // 2. On injecte le Repository dans le ViewModel
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}