package ch.tomgies.recipe.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipesApi {
    @GET("recipes?select=name,rating,difficulty,tags,image")
    suspend fun getRecipes(@Query("limit") limit: Int, @Query("skip") skip: Int) : Response<RecipesDTO>

    @GET("recipes/search?select=name,rating,difficulty,tags,image")
    suspend fun searchRecipes(@Query("q") query: String) : Response<RecipesDTO>
}