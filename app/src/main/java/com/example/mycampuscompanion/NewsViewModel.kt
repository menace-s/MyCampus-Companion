package com.example.mycampuscompanion
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch




// Représente les différents états possibles de notre écran d'actualités
sealed interface NewsState {
    object Loading : NewsState // L'état de chargement
    data class Success(val posts: List<Post>) : NewsState // L'état de succès avec la liste des articles
    data class Error(val message: String) : NewsState // L'état d'erreur avec un message
}

class NewsViewModel : ViewModel() {

    // _state est un "flux d'états" privé et modifiable.
    // On l'initialise à l'état de chargement.
    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)

    // state est la version publique et non-modifiable de _state.
    // L'interface (la Vue) ne pourra que l'observer, pas le modifier.
    val state: StateFlow<NewsState> = _state

    // Le bloc "init" est appelé automatiquement à la création du ViewModel.
    init {
        fetchPosts()
    }

    // La fonction qui lance l'appel réseau.
    private fun fetchPosts() {
        // On lance une nouvelle coroutine dans le "scope" du ViewModel.
        // Cette coroutine sera automatiquement annulée si le ViewModel est détruit.
        viewModelScope.launch {
            try {
                // On demande les articles à notre client Retrofit
                val posts = RetrofitClient.apiService.getPosts()
                // Si tout va bien, on met à jour l'état avec le résultat
                _state.value = NewsState.Success(posts)
            } catch (e: Exception) {
                // S'il y a une erreur (pas d'internet, serveur en panne...),
                // on met à jour l'état avec le message d'erreur.
                _state.value = NewsState.Error("Erreur de chargement : ${e.message}")
            }
        }
    }
}