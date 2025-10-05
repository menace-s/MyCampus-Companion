// data/local/PostDao.kt
package com.example.mycampuscompanion.data.local

import androidx.room.*
import com.example.mycampuscompanion.data.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object pour les opérations sur la table 'posts'
 *
 * Toutes les méthodes sont suspend car elles effectuent des opérations
 * de base de données qui doivent être exécutées en arrière-plan
 */
@Dao
interface PostDao {

    /**
     * Récupère tous les posts triés par date de publication (plus récent en premier)
     * @return Liste de tous les posts en cache
     */
    @Query("SELECT * FROM posts ORDER BY publishedAt DESC")
    suspend fun getAllPosts(): List<Post>

    /**
     * Récupère tous les posts sous forme de Flow (pour observer les changements)
     * Optionnel : utile si vous voulez observer les changements en temps réel
     */
    @Query("SELECT * FROM posts ORDER BY publishedAt DESC")
    fun getAllPostsFlow(): Flow<List<Post>>

    /**
     * Insère une liste de posts
     * Si un post existe déjà (même ID), il sera remplacé
     * @param posts Liste des posts à insérer
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    /**
     * Insère un seul post
     * @param post Le post à insérer
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    /**
     * Supprime tous les posts (pour rafraîchir le cache)
     */
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    /**
     * Supprime un post spécifique
     * @param post Le post à supprimer
     */
    @Delete
    suspend fun deletePost(post: Post)

    /**
     * Compte le nombre total de posts en cache
     * Utilisé pour vérifier si des données sont disponibles hors ligne
     * @return Le nombre de posts dans la base de données
     */
    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    /**
     * Récupère un post par son ID
     * @param postId L'ID du post recherché
     * @return Le post correspondant ou null
     */
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Int): Post?

    /**
     * Recherche des posts par titre ou description
     * @param searchQuery Le terme de recherche
     * @return Liste des posts correspondants
     */
    @Query("SELECT * FROM posts WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY publishedAt DESC")
    suspend fun searchPosts(searchQuery: String): List<Post>
}