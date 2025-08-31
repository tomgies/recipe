package ch.tomgies.recipe.domain.usecase

import ch.tomgies.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

interface SearchRecipesUseCase {
    suspend operator fun invoke(query: String): Result<Unit>
}

class SearchRecipesUseCaseImpl @Inject constructor(
    val recipeRepo: RecipeRepository
) : SearchRecipesUseCase {
    override suspend fun invoke(query: String): Result<Unit> = runCatching { recipeRepo.search(query) }
}