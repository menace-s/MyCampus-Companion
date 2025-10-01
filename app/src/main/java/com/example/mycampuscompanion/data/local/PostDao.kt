package com.example.mycampuscompanion.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mycampuscompanion.data.model.Post
import kotlinx.coroutines.flow.Flow

// 1. @Dao : On dit à Room que cette interface est un Data Access Object.
@Dao
interface PostDao {

    // 2. @Insert : Définit une fonction pour insérer des données.
    //    OnConflictStrategy.REPLACE signifie que si on insère une liste d'articles
    //    et que certains existent déjà (même clé primaire), ils seront remplacés.
    //    C'est parfait pour mettre à jour nos données.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    // 3. @Query : Permet d'écrire des requêtes SQL pour lire les données.
    //    "SELECT * FROM posts" signifie "Récupère toutes les colonnes de la table posts".
    //    La fonction retourne un Flow<List<Post>>, ce qui est génial : notre UI
    //    sera notifiée automatiquement à chaque fois que les données dans la table changent.
    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<Post>>

    // 4. @Query : Une autre requête pour supprimer toutes les données de la table.
    //    Utile pour vider le cache avant d'insérer une nouvelle liste fraîche.
    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}