package com.example.mycampuscompanion.data

import com.example.mycampuscompanion.data.local.PostDao
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val apiService: ApiService,
    private val postDao: PostDao
) {
    /**
     * Expose le flux de données directement depuis la base de données (Room).
     * C'est la "source de vérité" pour notre UI.
     */
    fun getPosts(): Flow<List<Post>> = postDao.getAllPosts()

    /**
     * Rafraîchit les données depuis le réseau (API) et les sauvegarde dans la base de données.
     * C'est une fonction suspendue car c'est une opération réseau.
     */
    suspend fun refreshPosts() {
        try {
            val freshPosts = apiService.getPosts()
            postDao.insertAll(freshPosts)
        } catch (e: Exception) {
            // En cas d'erreur réseau, on peut la logger. L'app continuera
            // d'afficher les données du cache, donc l'utilisateur ne verra pas d'écran vide.
            println("NewsRepository: Erreur de rafraîchissement réseau: ${e.message}")
        }
    }
}