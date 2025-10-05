// data/model/Post.kt
package com.example.mycampuscompanion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room représentant une actualité dans la base de données locale
 *
 * Cette classe sert de modèle unifié pour stocker les articles
 * provenant de différentes sources (NYTimes, etc.) dans SQLite
 */
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String?,

    val imageUrl: String?,

    val url: String,

    val source: String,

    val publishedAt: String,

    val isCached: Boolean = false // Indique si c'est un article en cache
)