package com.example.mycampuscompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.model.Report

// 1. @Database : On déclare que c'est une base de données Room.
// entities = [...] : On liste toutes les entités (tables) que cette base de données va gérer.
// version = 4 : Version actuelle de la base (vous l'avez déjà incrémentée)
// exportSchema = false : Évite de générer des fichiers de schéma (optionnel en dev)
@Database(
    entities = [Post::class, Report::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // 2. On déclare une fonction abstraite pour chaque DAO.
    // Room va générer le code pour nous fournir une instance de chaque DAO.
    abstract fun postDao(): PostDao
    abstract fun reportDao(): ReportDao

    // 3. Companion object pour créer un Singleton.
    // Cela garantit qu'on n'aura qu'UNE SEULE instance de la base de données
    // dans toute l'application, ce qui est très important pour la performance.
    companion object {
        // @Volatile garantit que la valeur de INSTANCE est toujours à jour
        // même si plusieurs threads y accèdent en même temps.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Méthode pour obtenir l'instance unique de la base de données
         */
        fun getInstance(context: Context): AppDatabase {
            // On utilise l'opérateur Elvis (?:) avec synchronized
            // pour créer l'instance seulement si elle n'existe pas déjà
            return INSTANCE ?: synchronized(this) {
                // Double vérification : on re-vérifie si l'instance n'a pas été
                // créée entre-temps par un autre thread
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mycampus_database" // Nom du fichier de la base de données
                )
                    // fallbackToDestructiveMigration() : En cas de changement de version
                    // sans migration définie, Room va SUPPRIMER et RECRÉER la base.
                    // ⚠️ Attention : cela efface toutes les données !
                    // En production, il faudrait créer des migrations appropriées.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Alternative compatible avec votre MainActivity actuelle
         * (qui utilise getDatabase au lieu de getInstance)
         */
        fun getDatabase(context: Context): AppDatabase {
            return getInstance(context)
        }
    }
}