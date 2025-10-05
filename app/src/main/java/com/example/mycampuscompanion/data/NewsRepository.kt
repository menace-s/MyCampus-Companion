// data/repository/NewsRepository.kt
package com.example.mycampuscompanion.data.repository

import com.example.mycampuscompanion.data.local.PostDao
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.remote.ApiService
import com.example.mycampuscompanion.data.remote.toPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository qui gère la logique de récupération des actualités NYTimes
 * - Essaie d'abord de récupérer depuis l'API
 * - En cas d'échec (pas de connexion), charge depuis le cache SQLite
 */
class NewsRepository(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val apiKey: String
) {

    /**
     * Récupère les actualités avec stratégie de cache
     *
     * @param section Section NYTimes : world, technology, science, business, etc.
     *
     * Flux de données :
     * 1. Tente un appel API NYTimes
     * 2. Si succès : met à jour le cache et retourne les données fraîches
     * 3. Si échec : retourne les données du cache local
     */
    fun getNews(section: String = "world"): Flow<Result<List<Post>>> = flow {
        try {
            // Log pour déboguer
            android.util.Log.d("NewsRepository", "Calling NYTimes API with section: $section")
            android.util.Log.d("NewsRepository", "API Key length: ${apiKey.length}")

            // 1. Tenter de récupérer depuis l'API NYTimes
            val response = apiService.getTopStories(
                section = section,
                apiKey = apiKey
            )

            android.util.Log.d("NewsRepository", "API Response status: ${response.status}")

            if (response.status == "OK") {
                // 2. Convertir les articles NYTimes en Posts (données fraîches = isCached false)
                val posts = response.results.map { it.toPost(isCached = false) }

                // 3. Mettre à jour le cache local (on marque comme cached pour le stockage)
                val cachedPosts = posts.map { it.copy(isCached = true) }
                postDao.deleteAllPosts() // Vider l'ancien cache
                postDao.insertPosts(cachedPosts)

                // 4. Émettre les données fraîches (avec isCached = false pour l'affichage)
                emit(Result.success(posts))
            } else {
                // Si l'API répond mais avec une erreur
                throw Exception("Erreur API: ${response.status}")
            }

        } catch (e: Exception) {
            // 5. En cas d'erreur (pas de connexion, timeout, erreur API, etc.)
            // Charger depuis le cache local
            val cachedPosts = postDao.getAllPosts()

            if (cachedPosts.isNotEmpty()) {
                // On a des données en cache, on les retourne
                emit(Result.success(cachedPosts))
            } else {
                // Aucune donnée disponible, ni en ligne ni en cache
                emit(Result.failure(
                    Exception("Aucune donnée disponible. Connectez-vous à Internet pour charger les actualités.\nDétails: ${e.message}")
                ))
            }
        }
    }

    /**
     * Récupère uniquement depuis le cache (pour usage hors ligne)
     */
    suspend fun getCachedNews(): List<Post> {
        return postDao.getAllPosts()
    }

    /**
     * Vérifie si des données sont en cache
     */
    suspend fun hasCachedData(): Boolean {
        return postDao.getPostCount() > 0
    }

    /**
     * Récupère les actualités d'une section spécifique
     * Sections disponibles : arts, business, health, science, sports, technology, world, etc.
     */
    fun getNewsBySection(section: String): Flow<Result<List<Post>>> {
        return getNews(section)
    }
}