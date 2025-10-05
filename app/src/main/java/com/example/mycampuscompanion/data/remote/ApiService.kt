// data/remote/ApiService.kt
package com.example.mycampuscompanion.data.remote

import com.example.mycampuscompanion.data.model.Post
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface Retrofit pour NYTimes API
 *
 * Limite gratuite : 500 requêtes par jour et par API
 * Documentation : https://developer.nytimes.com/docs/top-stories-product/1/overview
 *
 * Sections disponibles : arts, automobiles, books, business, fashion, food, health,
 * home, insider, magazine, movies, nyregion, obituaries, opinion, politics, realestate,
 * science, sports, sundayreview, technology, theater, t-magazine, travel, upshot, us, world
 */
interface ApiService {

    @GET("svc/topstories/v2/{section}.json")
    suspend fun getTopStories(
        @Path("section") section: String = "world",
        @Query("api-key") apiKey: String
    ): NYTimesResponse
}

/**
 * Modèles de réponse NYTimes API
 */
data class NYTimesResponse(
    val status: String,
    val copyright: String?,
    val section: String?,
    val last_updated: String?,
    val num_results: Int,
    val results: List<NYTimesArticle>
)

data class NYTimesArticle(
    val section: String,
    val subsection: String?,
    val title: String,
    val abstract: String,
    val url: String,
    val uri: String?,
    val byline: String?,
    val item_type: String?,
    val updated_date: String?,
    val created_date: String?,
    val published_date: String,
    val material_type_facet: String?,
    val kicker: String?,
    val des_facet: List<String>?,
    val org_facet: List<String>?,
    val per_facet: List<String>?,
    val geo_facet: List<String>?,
    val multimedia: List<NYTimesMedia>?,
    val short_url: String?
)

data class NYTimesMedia(
    val url: String,
    val format: String,
    val height: Int,
    val width: Int,
    val type: String,
    val subtype: String,
    val caption: String?,
    val copyright: String?
)

/**
 * Extension pour convertir un NYTimesArticle en Post de notre base de données
 * @param isCached Indique si c'est une donnée du cache (false par défaut = données fraîches)
 */
fun NYTimesArticle.toPost(isCached: Boolean = false): Post {
    // Priorité : Standard Thumbnail → mediumThreeByTwo210 → première image disponible
    val imageUrl = this.multimedia?.firstOrNull {
        it.format == "Standard Thumbnail" || it.format == "mediumThreeByTwo210"
    }?.url ?: this.multimedia?.firstOrNull()?.url

    return Post(
        title = this.title,
        description = this.abstract,
        imageUrl = imageUrl,
        url = this.url,
        source = "NYTimes - ${this.section}",
        publishedAt = this.published_date,
        isCached = isCached
    )
}

/**
 * Objet singleton pour créer l'instance Retrofit
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.nytimes.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}