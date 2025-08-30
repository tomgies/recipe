package ch.tomgies.recipe.ui.recipes

import ch.tomgies.recipe.domain.entity.Recipe

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isReloading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: Throwable? = null
)