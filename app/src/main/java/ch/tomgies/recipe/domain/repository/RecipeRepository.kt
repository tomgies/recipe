package ch.tomgies.recipe.domain.repository

import ch.tomgies.recipe.domain.entity.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    val recipes: Flow<List<Recipe>>
    suspend fun loadNextPage()
    suspend fun reload()
}