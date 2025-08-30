package ch.tomgies.recipe.data.repository

import ch.tomgies.recipe.domain.entity.Recipe
import ch.tomgies.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class RecipeRepositoryImpl() : RecipeRepository {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: Flow<List<Recipe>> = _recipes

    override suspend fun loadNextPage() = withContext(Dispatchers.IO) {
        val nextPage = fetchRecipesFromServer(skip = _recipes.value.size, limit = 10)
        val updatedElements = _recipes.value + nextPage
        _recipes.emit(updatedElements)
    }

    override suspend fun reload() {
        val recipes = fetchRecipesFromServer(skip = 0, limit = 10)
        _recipes.emit(recipes)
    }

    private suspend fun fetchRecipesFromServer(skip: Int, limit: Int): List<Recipe> {
        delay(500)
        val newElements = mockData.drop(skip).take(limit)
        return newElements
    }

    private val mockData = (1..100).map { id ->
        Recipe(id, "Classic Margherita Pizza", 4.6, "Easy", listOf("Pizza", "Italian"))
    }
}