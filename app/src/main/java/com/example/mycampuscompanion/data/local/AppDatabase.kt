package com.example.mycampuscompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycampuscompanion.data.model.Post
import com.example.mycampuscompanion.data.model.Report

// 1. @Database : On déclare que c'est une base de données Room.
//    entities = [...] : On liste toutes les entités (tables) que cette base de données va gérer.
//    version = 1 : C'est la version de notre base. Si on change la structure, on devra l'augmenter.
@Database(entities = [Post::class,Report::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    // 2. On déclare une fonction abstraite pour chaque DAO.
    //    Room va générer le code pour nous fournir une instance de PostDao.
    abstract fun postDao(): PostDao
    abstract fun reportDao(): ReportDao

    // 3. Companion object pour créer un Singleton.
    //    Cela garantit qu'on n'aura qu'UNE SEULE instance de la base de données
    //    dans toute l'application, ce qui est très important pour la performance.
    companion object {
        // @Volatile garantit que la valeur de INSTANCE est toujours à jour.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // On utilise synchronized pour éviter que plusieurs threads créent
            // la base de données en même temps.
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "my_campus_database" // Le nom du fichier de la base de données sur le téléphone
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}