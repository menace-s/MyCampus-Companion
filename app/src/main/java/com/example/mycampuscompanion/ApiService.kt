package com.example.mycampuscompanion

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- 1. Le "moule" pour nos données ---
// Cette data class représente un article ("Post") de notre API.
// Les noms des variables doivent correspondre aux clés du JSON.
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    // @SerializedName est utile si le nom dans le JSON est différent
    // de celui qu'on veut en Kotlin. Ici, "body" est le même.
    @SerializedName("body")
    val content: String
)


// --- 2. Le "menu" des actions possibles avec l'API ---
// C'est ici qu'on liste toutes les "commandes" qu'on peut passer.
interface ApiService {
    // @GET("posts") signifie : "Fais une requête HTTP GET vers l'endpoint 'posts'".
    // L'endpoint est ajouté à l'URL de base. Donc, l'URL complète sera :
    // https://jsonplaceholder.typicode.com/posts
    // La fonction est "suspend" car c'est une opération longue (un appel réseau).
    // Elle doit être appelée depuis une coroutine pour ne pas bloquer l'interface.
    @GET("posts")
    suspend fun getPosts(): List<Post>
}


// --- 3. L'objet qui construit et nous donne accès à notre client Retrofit ---
// On utilise un "object" pour en faire un Singleton, c'est-à-dire qu'il n'y aura
// qu'une seule instance de cet objet dans toute notre application.
object RetrofitClient {

    // L'URL de base de notre API
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // On crée une instance de Retrofit en utilisant un "lazy" delegate.
    // Cela signifie que le code à l'intérieur ne sera exécuté que la première fois
    // qu'on y accède, et pas avant. C'est une bonne pratique pour la performance.
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // On définit l'URL de base
            // On ajoute le convertisseur qui va transformer le JSON en nos data class Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // Et enfin, on dit à Retrofit de créer une implémentation de notre "menu" (ApiService)
            .create(ApiService::class.java)
    }
}