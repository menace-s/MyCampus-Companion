package com.example.mycampuscompanion.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    // @SerializedName est utile si le nom dans le JSON est différent
    // de celui qu'on veut en Kotlin. Ici, "body" est le même.
    @SerializedName("body")
    val content: String
)