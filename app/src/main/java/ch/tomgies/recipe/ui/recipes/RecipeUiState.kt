package ch.tomgies.recipe.ui.recipes

sealed class RecipeUiState(open val recipes: List<String>) {
    data object LoadingState : RecipeUiState(emptyList())
    data class DataState(
        override val recipes: List<String>,
        val isLoadingPage: Boolean
    ) : RecipeUiState(recipes)

    data class ErrorState(
        val error: Throwable,
        override val recipes: List<String> = emptyList()
    ) : RecipeUiState(recipes)
}