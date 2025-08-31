package ch.tomgies.recipe.domain.usecase

import ch.tomgies.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

interface LoadNextRecipesPageUseCase {
    suspend operator fun invoke(): Result<Unit>
}

class LoadNextRecipesPageUseCaseImpl @Inject constructor(
    val recipeRepo: RecipeRepository
) : LoadNextRecipesPageUseCase {
    override suspend fun invoke(): Result<Unit> = runCatching { recipeRepo.loadNextPage() }
}