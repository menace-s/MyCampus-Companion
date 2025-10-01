package com.example.mycampuscompanion.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// 1. @Entity : On dit à Room que cette data class est une table de base de données.
//    tableName = "posts" est le nom que la table aura dans la base de données.
@Entity(tableName = "posts")
data class Post(

    // 2. @PrimaryKey : On dit à Room que le champ 'id' est la clé primaire.
    //    Chaque article aura un 'id' unique, ce qui permet de l'identifier sans ambiguïté.
    @PrimaryKey
    val id: Int,

    // 3. @ColumnInfo : Permet de donner un nom différent à la colonne dans la base de données.
    //    C'est une bonne pratique d'utiliser le "snake_case" (ex: user_id) pour les noms de colonnes.
    @ColumnInfo(name = "user_id")
    val userId: Int,

    val title: String,

    // 4. On a maintenant deux annotations pour ce champ !
    // @SerializedName("body") : Pour Gson/Retrofit, pour lire le JSON.
    // @ColumnInfo(name = "content_body") : Pour Room, pour nommer la colonne dans la base de données.
    @SerializedName("body")
    @ColumnInfo(name = "content_body")
    val content: String
)