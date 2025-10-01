package com.example.mycampuscompanion.ui.features.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycampuscompanion.data.model.Post

object NewsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras // Ce paramètre "extras" est la nouvelle façon de faire
    ): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            // On récupère l'application via les "extras" de manière sûre
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun NewsScreen(newsViewModel: NewsViewModel = viewModel(factory = NewsViewModelFactory)) {

    // 1. On observe le "state" du ViewModel.
    // `collectAsStateWithLifecycle` est intelligent : il transforme le StateFlow du ViewModel
    // en un objet State de Compose et ne l'observe que lorsque l'écran est visible.
    val state by newsViewModel.state.collectAsStateWithLifecycle()

    // 2. On utilise un "when" pour afficher la bonne interface en fonction de l'état.
    when (val currentState = state) {
        is NewsState.Loading -> {
            // Affiche une icône de chargement au centre de l'écran
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is NewsState.Success -> {
            // Affiche la liste des articles
            NewsList(posts = currentState.posts)
        }
        is NewsState.Error -> {
            // Affiche le message d'erreur au centre de l'écran
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentState.message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// Un Composable dédié à l'affichage de la liste des articles
@Composable
fun NewsList(posts: List<Post>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espace entre les éléments
    ) {
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}

// Un Composable pour afficher un seul article, sous forme de carte
@Composable
fun PostCard(post: Post) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.title.replaceFirstChar { it.uppercase() }, // Met la première lettre en majuscule
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = post.content.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}