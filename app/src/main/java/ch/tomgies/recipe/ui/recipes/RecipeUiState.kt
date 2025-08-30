package ch.tomgies.recipe.ui.recipes

import ch.tomgies.recipe.domain.entity.Recipe

sealed class RecipeUiState(open val recipes: List<Recipe>) {
    data object LoadingState : RecipeUiState(emptyList())
    data class DataState(
        override val recipes: List<Recipe>,
        val isLoadingPage: Boolean
    ) : RecipeUiState(recipes)

    data class ErrorState(
        val error: Throwable,
        override val recipes: List<Recipe> = emptyList()
    ) : RecipeUiState(recipes)
}