package ch.tomgies.recipe.data.repository

import ch.tomgies.recipe.data.api.RecipesApi
import ch.tomgies.recipe.domain.entity.Recipe
import ch.tomgies.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipesApi: RecipesApi
) : RecipeRepository {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: Flow<List<Recipe>> = _recipes

    override suspend fun reload() {
        val recipes = fetchRecipesFromServer(skip = 0, limit = 20)
        _recipes.emit(recipes)
    }

    override suspend fun loadNextPage() = withContext(Dispatchers.IO) {
        val nextPage = fetchRecipesFromServer(skip = _recipes.value.size, limit = 10)
        val updatedElements = _recipes.value + nextPage
        _recipes.emit(updatedElements)
    }

    override suspend fun search(query: String) {
        val response = recipesApi.searchRecipes(query)
        if (response.isSuccessful && response.body() != null) {
            _recipes.emit(response.body()!!.recipes)
        } else {
            _recipes.emit(emptyList())
        }
    }

    private suspend fun fetchRecipesFromServer(skip: Int, limit: Int): List<Recipe> {
        val response = recipesApi.getRecipes(limit = limit, skip = skip)
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.recipes
        } else {
            emptyList()
        }
    }
}