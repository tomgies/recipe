package ch.tomgies.recipe.domain.usecase

import ch.tomgies.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

interface ReloadRecipesUseCase {
    suspend operator fun invoke(): Result<Unit>
}

class ReloadRecipesUseCaseImpl @Inject constructor(
    val recipeRepo: RecipeRepository
) : ReloadRecipesUseCase {
    override suspend fun invoke(): Result<Unit> = runCatching { recipeRepo.reload() }
}